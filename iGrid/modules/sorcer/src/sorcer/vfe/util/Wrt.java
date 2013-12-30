package sorcer.vfe.util;

import java.util.ArrayList;
import java.util.List;

public class Wrt extends ArrayList<String> {
		
		static final long serialVersionUID = 3305090121860355856L;
		
		public Wrt() {
			super();
		}
		
		public Wrt(String wrt) {
			add(wrt);
		}
		
		public Wrt(String[] wrt) {
			for (String w : wrt)
				add(w);
		}
		
		public Wrt(List<String> wrtNames) {
			addAll(wrtNames);
		}
		
		 public String[] toArray() {
			 String[] sa = new String[size()];
			 return toArray(sa);
		 }
	}