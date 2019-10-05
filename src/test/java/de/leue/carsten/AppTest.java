package de.leue.carsten;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import com.github.davidmoten.rx2.Bytes;

import de.leue.carsten.rx.jackson.RxJackson;
import io.reactivex.Flowable;
import io.reactivex.FlowableOperator;
import io.reactivex.FlowableTransformer;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	
	public static FlowableTransformer<String, String> toMap() {
		return src$ -> src$.map(a -> a + "-test");
	}
	
	@Test
	public void shouldParseJson() throws Exception {

		final InputStream is = getClass().getResourceAsStream("/test.json");
		final Flowable<byte[]> in$ = Bytes.from(is, 5);
		
		final JsonFactory fct = JsonFactory.builder().build();
		
		// parse
		final Flowable<Object> entries$ = in$.compose(RxJackson.parseByteArray(() -> fct.createNonBlockingByteArrayParser())).doOnNext(obj -> System.out.println(obj));
		
		final TestSubscriber<Object> subscriber = new TestSubscriber<>();
		entries$.subscribe(subscriber);
		
		subscriber.assertComplete();
	}
	
	@Test
	public void shouldCreateParser() throws IOException {
	
		final JsonFactory f = new JsonFactory();
		final JsonParser p = f.createNonBlockingByteArrayParser();
		
		
		final NonBlockingInputFeeder feeder = p.getNonBlockingInputFeeder();
	}
	
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
