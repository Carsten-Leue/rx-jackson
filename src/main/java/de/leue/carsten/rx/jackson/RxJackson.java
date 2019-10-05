package de.leue.carsten.rx.jackson;

import static com.fasterxml.jackson.core.JsonToken.NOT_AVAILABLE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.async.ByteBufferFeeder;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.BiConsumer;

public class RxJackson {

	private static final void feedByteBuffer(final ByteBuffer buffer, final JsonParser parser) throws IOException {
		final ByteBufferFeeder feeder = (ByteBufferFeeder) parser.getNonBlockingInputFeeder();
		feeder.feedInput(buffer);
	}

	private static final void feedByteArray(final byte[] buffer, final JsonParser parser) throws IOException {
		final ByteArrayFeeder feeder = (ByteArrayFeeder) parser.getNonBlockingInputFeeder();
		feeder.feedInput(buffer, 0, buffer.length);
	}

	public static FlowableTransformer<ByteBuffer, Object> parseByteBuffer(final Callable<? extends JsonParser> aSeed) {
		return parseJson(aSeed, RxJackson::feedByteBuffer);
	}

	public static FlowableTransformer<byte[], Object> parseByteArray(final Callable<? extends JsonParser> aSeed) {
		return parseJson(aSeed, RxJackson::feedByteArray);
	}

	private static void exhaustTokens(final List<Object> tokens, JsonParser parser) throws IOException {
		JsonToken t;
		while (((t = parser.nextToken()) != NOT_AVAILABLE) && (t != null)) {
			// append
			tokens.add(t);
			// handle token type
			switch (t) {
			case FIELD_NAME:
			case VALUE_STRING:
				tokens.add(parser.getText());
				break;
			case VALUE_NUMBER_FLOAT:
			case VALUE_NUMBER_INT:
				tokens.add(parser.getNumberValue());
				break;
			case VALUE_TRUE:
				tokens.add(TRUE);
				break;
			case VALUE_FALSE:
				tokens.add(FALSE);
				break;
			default:
				break;
			}
		}
	}

	private static <T> FlowableTransformer<T, Object> parseJson(final Callable<? extends JsonParser> aSeed,
			final BiConsumer<? super T, ? super JsonParser> aFeeder) {
		// returns our operator
		return src$ -> Flowable.using(aSeed, (parser) -> src$.concatMap(buffer -> {
			// convert the tokens into an array
			final ArrayList<Object> tokens = new ArrayList<>();
			aFeeder.accept(buffer, parser);
			exhaustTokens(tokens, parser);
			// return the sequence
			return Flowable.fromIterable(tokens);
		}), parser -> parser.close());

	}

}
