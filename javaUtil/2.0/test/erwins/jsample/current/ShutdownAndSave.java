
package erwins.jsample.current;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import net.jcip.annotations.GuardedBy;

/**
 * 셧다운 후 중단된 내용을 저장.
 */
public abstract class ShutdownAndSave {
    private volatile ShutdownAndSeeForcedRun exec;
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<URL>();

    private final ConcurrentMap<URL, Boolean> seen = new ConcurrentHashMap<URL, Boolean>();
    private static final long TIMEOUT = 500;
    private static final TimeUnit UNIT = MILLISECONDS;

    public ShutdownAndSave(URL startUrl) {
        urlsToCrawl.add(startUrl);
    }

    public synchronized void start() {
        exec = new ShutdownAndSeeForcedRun(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl)
            submitCrawlTask(url);
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(TIMEOUT, UNIT)) saveUncrawled(exec.getCancelledTasks());
        }
        finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable task : uncrawled)
            urlsToCrawl.add(((CrawlTask) task).getPage());
    }

    private void submitCrawlTask(URL u) {
        exec.execute(new CrawlTask(u));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        CrawlTask(URL url) {
            this.url = url;
        }

        //private int count = 1;

        @SuppressWarnings("unused")
		boolean alreadyCrawled() {
            return seen.putIfAbsent(url, true) != null;
        }

        @SuppressWarnings("unused")
		void markUncrawled() {
            seen.remove(url);
            System.out.printf("marking %s uncrawled%n", url);
        }

        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) return;
                submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return url;
        }
    }
}
