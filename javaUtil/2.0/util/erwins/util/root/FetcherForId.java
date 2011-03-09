package erwins.util.root;



/**
 * 캐시에 일단 사용된다. 보통 DAO에 붙여서 사용된다.
 */
public interface FetcherForId<ID,Entity>{
	public Entity getById(ID id);
}