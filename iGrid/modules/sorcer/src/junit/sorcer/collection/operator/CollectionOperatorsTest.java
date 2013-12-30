package junit.sorcer.collection.operator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.array;
import static sorcer.co.operator.bag;
import static sorcer.co.operator.dictionary;
import static sorcer.co.operator.entry;
import static sorcer.co.operator.key;
import static sorcer.co.operator.list;
import static sorcer.co.operator.listContext;
import static sorcer.co.operator.map;
import static sorcer.co.operator.value;
import static sorcer.eo.operator.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.co.tuple.FidelityEntry;
import sorcer.co.tuple.StrategyEntry;
import sorcer.co.tuple.Tuple2;
import sorcer.co.tuple.Tuple3;
import sorcer.core.context.ListContext;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;

/**
 * @author Mike Sobolewski
 */
public class CollectionOperatorsTest {
	private final static Logger logger = Logger.getLogger(CollectionOperatorsTest.class.getName());
	
	@Test
	public void arrayOperatorTest() throws EvaluationException {
		Double[] doubles = array(1.1, 2.1, 3.1);
		//logger.info("length " + ((Double[])doubles).length);
		assertArrayEquals(doubles, new Double[] { 1.1, 2.1, 3.1 }   );
		
		Object array = array(array(1.1, 2.1, 3.1),  4.1,  array(11.1, 12.1, 13.1));
//		logger.info("array " + SorcerUtil.arrayToString(array));
		
		assertArrayEquals((Double[])((Object[])array)[0], array(1.1, 2.1, 3.1));
		assertEquals(((Object[])array)[1], 4.1);
		assertArrayEquals((Double[])((Object[])array)[2], array(11.1, 12.1, 13.1));
	}
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void listOperatorTest() throws EvaluationException {
		List<Object> o_list = list(list(1.1, 2.1, 3.1),  4.1,  list(11.1, 12.1, 13.1));
		
		List<Double> d_list = (List<Double>)o_list.get(0);
		assertEquals(d_list, Arrays.asList(array(1.1, 2.1, 3.1)));
		
		assertEquals(o_list.get(0), list(1.1, 2.1, 3.1));
		assertEquals(o_list.get(1), 4.1);
		assertEquals(o_list.get(2), list(11.1, 12.1, 13.1));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void mapOperatorTest() throws EvaluationException {
		Map<Object, Object> map1 = dictionary(entry("name", "Mike"), entry("height", 174.0));
				
		Map<String, Double> map2 = map(entry("length", 248.0), entry("width", 2.0), entry("height", 17.0));
		
		// keys and values of entries
		String k = key(entry("name", "Mike"));
		Double v = value(entry("height", 174.0));
		assertEquals(k, "name");
		assertTrue(v.equals(174.0));
		
		// casts are needed for dictionary: Map<Object, Object>
		k = (String)map1.get("name");
		v = (Double)map1.get("height");
		assertEquals(k, "Mike");
		assertTrue(v.equals(174.0));
		
		// casts are NOT needed for map: Map<K, V>
		v = map2.get("length");
		assertTrue(v.equals(248.0));
		
		// check map keys
		assertEquals(map1.keySet(), bag("name", "height"));
		// check map values
		assertArrayEquals(map1.values().toArray(), (array(174.0, "Mike")));
		
	}
	
	@Test
	public void bagOperatorTest() throws EvaluationException {
		// the bag operator creates instances of java.util.Set
		Set<Object> set = bag("name", "Mike", "name", "Ray", (Object)entry("height", 174));
		assertEquals(set.size(), 4);
		assertEquals(entry("height", 174), entry("height", 174));
		assertTrue(set.contains(entry("height", 174)));
	}
	
	@Test
	public void listContextOperatorTest() throws ContextException {
		ListContext<Double> context = listContext(1.1, 1.2, 1.3, 1.4, 1.5);
		//logger.info(" index 1: " + context.get(1));
		assertEquals(context.get(1), 1.2);
		context.putValue(1, 5.0);
		assertEquals(context.get(1), 5.0);
		//logger.info("context path 1: " + context.pathFor(1));
		assertEquals(context.pathFor(1), "element[1]");
		//logger.info("list context: " + context);
		//logger.info("elements: " + context.getElements());
		//context.putValue("element[1]", 10.0);
		assertEquals(context.getElements(), list(1.1, 5.0, 1.3, 1.4, 1.5));
		context.set(1, 20.0);
		assertEquals(20.0, context.get(1));
		assertEquals(context.add(30.0), true);
		assertEquals(context.get(5), 30.0);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void entriesTest() throws ContextException {
		Tuple2 e2 = entry("x1", 10.0);
		//logger.info("tuple e2: " + e2);
		assertEquals("x1", e2.key());
		assertEquals(10.0, e2.value());
			
		StrategyEntry se = entry("j1/j2", strategy(Access.PULL, Flow.PAR));
		//logger.info("tuple se: " + se);
		assertEquals(se.strategy().getFlowType(), Flow.PAR);
		assertEquals(se.strategy().getAccessType(), Access.PULL);

	}
}
