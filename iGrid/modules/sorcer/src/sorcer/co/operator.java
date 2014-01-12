/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sorcer.co;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sorcer.co.tuple.AnnotatedEntry;
import sorcer.co.tuple.Entry;
import sorcer.co.tuple.StrategyEntry;
import sorcer.co.tuple.Tuple1;
import sorcer.co.tuple.Tuple2;
import sorcer.co.tuple.Tuple3;
import sorcer.co.tuple.Tuple4;
import sorcer.co.tuple.Tuple5;
import sorcer.co.tuple.Tuple6;
import sorcer.core.context.ListContext;
import sorcer.service.ContextException;
import sorcer.service.Strategy;

public class operator {

	private static int count = 0;

	public static <T1> Tuple1<T1> x(T1 x1 ){
		return new Tuple1<T1>( x1 );
	}
	
	public static <T1> Tuple1<T1> tuple(T1 x1 ){
		return new Tuple1<T1>( x1 );
	}
	
	public static <T1,T2> Tuple2<T1,T2> x(T1 x1, T2 x2 ){
		return new Tuple2<T1,T2>( x1, x2 );
	}
	
	public static <T1,T2> Tuple2<T1,T2> tuple(T1 x1, T2 x2 ){
		return new Tuple2<T1,T2>( x1, x2 );
	}
	
	public static <T1,T2,T3> Tuple3<T1,T2,T3> x(T1 x1, T2 x2, T3 x3 ){
		return new Tuple3<T1,T2,T3>( x1, x2, x3 );
	}
	
	public static <T1,T2,T3> Tuple3<T1,T2,T3> tuple(T1 x1, T2 x2, T3 x3 ){
		return new Tuple3<T1,T2,T3>( x1, x2, x3 );
	}
	
	public static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> x(T1 x1, T2 x2, T3 x3, T4 x4 ){
		return new Tuple4<T1,T2,T3,T4>( x1, x2, x3, x4 );
	}
	
	public static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> tuple(T1 x1, T2 x2, T3 x3, T4 x4 ){
		return new Tuple4<T1,T2,T3,T4>( x1, x2, x3, x4 );
	}
	
	public static <T1,T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> x(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5 ){
		return new Tuple5<T1,T2,T3,T4,T5>( x1, x2, x3, x4, x5 );
	}
	
	public static <T1,T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> tuple(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5 ){
		return new Tuple5<T1,T2,T3,T4,T5>( x1, x2, x3, x4, x5 );
	}
	
	public static <T1,T2,T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> x(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5, T6 x6 ){
		return new Tuple6<T1,T2,T3,T4,T5,T6>( x1, x2, x3, x4, x5, x6 );
	}
	
	public static <T1,T2,T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> tuple(T1 x1, T2 x2, T3 x3, T4 x4, T5 x5, T6 x6 ){
		return new Tuple6<T1,T2,T3,T4,T5,T6>( x1, x2, x3, x4, x5, x6 );
	}
	
	public static String[] from(String... elems) {
		return elems;
	}
	
	public static <T> T[] array(T... elems) {
		return elems;
	}
	
	public static <T> Set<T> bag(T... elems) {
		return new HashSet<T>(list(elems));
	}

	public static <T> List<T> list(T... elems) {
		List<T> out = new ArrayList<T>(elems.length);
		for (T each : elems) {
			out.add(each);
		}
		return out;
	}

	public static List<Object> row(Object... elems) {
		return Arrays.asList(elems);
	}
	
	public static List<Object> values(Object... elems) {
		List<Object> list = new ArrayList<Object>();
		for(Object o: elems) {
			list.add(o);
		}
		return list;
	}

	public static List<String> header(String... elems) {
		List<String> out = new header<String>(elems.length);
		for (String each : elems) {
			out.add(each);
		}
		return out;
	}

	public static List<String> names(String... elems) {
		List<String> out = new ArrayList<String>(elems.length);
		for (String each : elems) {
			out.add(each);
		}
		return out;
	}

	public static <T1, T2> Tuple2<T1, T2> duo(T1 x1, T2 x2) {
		return new Tuple2<T1, T2>(x1, x2);
	}

	public static <T1, T2, T3> Tuple3<T1, T2, T3> triplet(T1 x1, T2 x2, T3 x3) {
		return new Tuple3<T1, T2, T3>(x1, x2, x3);
	}

	public static <T2> Entry<T2> entry(String x1, T2 x2) {
		return new Entry<T2>(x1, x2);
	}
	public static <T2> Entry<T2> entry(String x1) {
		return new Entry<T2>(x1, null);
	}
	
	public static <T> AnnotatedEntry<T> entry(String x1, String tag, T x2) {
		return new AnnotatedEntry<T>(x1, tag, x2);
	}
	
	public static <T2> Entry<T2> dbEntry(String x1, T2 x2) {
		Entry<T2> t2 = new Entry<T2>(x1, x2);
		t2.isPersistant = true;
		return t2;
	}
	
	public static <T1, T2> Tuple2<T1, T2> dbEntry(T1 x1, T2 x2, URL dbURL) {
		Tuple2<T1, T2> t2 = new Tuple2<T1, T2>(x1, x2);
		t2.isPersistant = true;
		t2.datastoreURL = dbURL;
		return t2;
	}
	
	public static <T1, T2, T3> Tuple3<T1, T2, T3> entry(T1 x1, T2 x2, T3 x3) {
		return new Tuple3<T1, T2, T3>(x1, x2, x3);
	}
	
	public static StrategyEntry entry(String x1, Strategy x2) {
		return new StrategyEntry(x1, x2);
	}
	
	public static <T1, T2> T1 key(Tuple2<T1, T2> entry) {
		return entry._1;
	}
	
	public static <T1, T2> T2 value(Tuple2<T1, T2> entry) {
		return entry._2;
	}

	public static <T extends Object> ListContext<T> listContext(T... elems)
			throws ContextException {
		ListContext<T> lc = new ListContext<T>();
		for (int i = 0; i < elems.length; i++) {
			lc.add(elems[i]);
		}
		return lc;
	}

	public static Map<Object, Object> dictionary(Tuple2<?, ?>... entries) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (Tuple2<?, ?> entry : entries) {
			map.put(entry._1, entry._2);
		}
		return map;
	}
	
	public static <K, V> Map<K, V> map(Tuple2<K, V>... entries) {
		Map<K, V> map = new HashMap<K, V>();
		for (Tuple2<K, V> entry : entries) {
			map.put(entry._1, entry._2);
		}
		return map;
	}
	
	public static Loop loop(int to) {
		Loop loop = new Loop(to);
		return loop;
	}

	public static Loop loop(int from, int to) {
		Loop loop = new Loop(from, to);
		return loop;
	}
	
	public static Loop loop(String template, int to) {
		Loop loop = new Loop(template, 1, to);
		return loop;
	}
	
	public static Loop loop(List<String> templates, int to) {
		Loop loop = new Loop(templates, to);
		return loop;
	}
	
	public static Loop loop(String template, int from, int to) {
		Loop loop = new Loop(template, from, to);
		return loop;
	}
	
	public static List<String> names(Loop loop, String prefix) {
		return loop.getNames(prefix);
	}
	
	public static String[] names(String name, int size, int from) {
		List<String> out = new ArrayList<String>();
		for (int i = from - 1; i < from + size - 1; i++) {
			out.add(name + (i + 1));
		}
		String[] names = new String[size];
		out.toArray(names);
		return names;
	}
	
	private static String getUnknown() {
		return "unknown" + count++;
	}
	
	private static String getUnknown(String name) {
		return name + count++;
	}
	
	static class header<T> extends ArrayList<T> {
		private static final long serialVersionUID = 1L;

		public header() {
			super();
		}
		
		public header(int initialCapacity) {
			super(initialCapacity);
		}
	}
}