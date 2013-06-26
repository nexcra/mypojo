package erwins.util.nio;

/** 스래드를 별도로 기동하는 등 셧다운해야 할것들을 지정한다. 종료시 일괄 셧다운 하자. */
public interface Shutdownable {

	public abstract void shutdown();

}