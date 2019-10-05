package de.leue.carsten;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;

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
	public void shouldCreateOperator() throws Exception {
		
		final Flowable<String> f$ = Flowable.just("Carsten");
		
		final Flowable<String> result$ = f$.compose(toMap());
		
		final TestSubscriber<String> subscriber = new TestSubscriber<>();
		result$.subscribe(subscriber);
		

		
		subscriber.assertComplete();
		subscriber.assertValues("Carsten-test");
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
