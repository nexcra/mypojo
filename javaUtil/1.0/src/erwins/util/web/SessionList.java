package erwins.util.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionList<T> {
	
	private final String key;

	public SessionList(String key) {
		this.key = key;
	}

	@SuppressWarnings("unchecked")
	public List<T> get(HttpServletRequest req) {
		HttpSession session = req.getSession();
		List<T> list = (List<T>) session.getAttribute(key);
		if (list == null) {
			list = new ArrayList<T>();
			session.setAttribute(key, list);
		}
		return list;
	}

	public void put(HttpServletRequest req, T value) {
		List<T> list = get(req);
		list.add(value);
	}

	public void clean(HttpServletRequest req) {
		HttpSession session = req.getSession();
		session.setAttribute(key, null);
	}
}