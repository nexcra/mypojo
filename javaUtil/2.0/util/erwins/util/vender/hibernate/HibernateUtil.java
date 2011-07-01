package erwins.util.vender.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;


/**
 * 스프링이 없을 때 사용하자.
 */
public class HibernateUtil {

	private static final Log log = LogFactory.getLog(HibernateUtil.class);

	private static final SessionFactory sf;

	static {
		try {
			sf = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			log.fatal("fail to initialize Hibernate SessionFactory", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sf;
	}

	public static Session getCurrentSession() {
		return sf.getCurrentSession();
	}

	public static void closeSession() {
		sf.getCurrentSession().close();
	}

	public static Transaction beginTransaction() {
		return sf.getCurrentSession().beginTransaction();
	}

	public static void commitTransaction() {
		sf.getCurrentSession().getTransaction().commit();
	}

	public static void rollbackTransaction() {
		if (sf.getCurrentSession().isOpen()) {
			Transaction tx = sf.getCurrentSession().getTransaction();
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
	}
}
