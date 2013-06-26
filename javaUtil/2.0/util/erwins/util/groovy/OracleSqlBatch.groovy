package erwins.util.groovy


import java.sql.Timestamp

import org.apache.poi.ss.formula.functions.T

import erwins.util.dateTime.ShowTime
import erwins.util.text.StringUtil
import erwins.util.vender.apache.Poi
import erwins.util.vender.apache.PoiReaderFactory
import erwins.util.vender.etc.OpenCsv


public class OracleSqlBatch{
	
	final OracleSql db;
	public OracleSqlBatch(OracleSql db){
		this.db = db;
	}

	/** sql을 페이징처리해서 콜백 돌린다. 대규모 자료 변환에 사용하자.
	 * ex) def columns = columns(TABLE_NAME)
	 * insertList(~~)
	 *  */
	public void batch(tableName,start,batchSize,maxSize,callback,sqlAppend=''){
		int maxCount = db.one("select COUNT(*) as COUNT from $tableName $sqlAppend")['COUNT']
		int maxPage = Math.round( maxCount / batchSize )
		long beforeTime = 0
		long cumulatedTime
		long finalPage = maxPage > maxSize ? maxSize : maxPage //둘중 작은게 결정자이다.
		for(int i=start;i<finalPage+1;i++){
			long startTime = System.nanoTime()
			if(beforeTime==0) println "$i / $maxPage 파일 처리중...."
			else{
				def nowTime = new ShowTime(beforeTime).toString()
				long avg = Math.round(cumulatedTime /  (i-start))
				def avgTime = new ShowTime( avg ).toString()
				def maxTime = new ShowTime(avg * (finalPage - i + 1)).toString()
				println "$i / $maxPage 파일 처리중... 이전배치동작시간 $nowTime / 평균 $avgTime / 남은예상시간 $maxTime "
			}
			def list = db.paging "select * from $tableName "+sqlAppend,batchSize,i
			if(list.size()==0){
				println '데이터가 없습니다.'
				return;
			}
			callback(list,i);
			if(list.size() < batchSize) {
				println '마지막 자료입니다. 배치를 종료합니다.'
				return
			}
			if(i==maxSize) println '배치의 maxSize에 도달했습니다. 배치를 종료합니다.'
			beforeTime = System.nanoTime() - startTime
			cumulatedTime += beforeTime
		}
	}
	
	/** DB의 내용을 csv로 옮긴다.
	* 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수
	* ex) db.dbToXls ROOT, 500, 2, " and TABLE_NAME = 'AGR_AGRI_DAY' "
	* 전부 다 하고싶으면 maxSize를 매우 높게 주도록 하자.  */
	public dbToCsv(dir,batchSize,maxSize,where = ''){
		db.loadInfo(where).loadCount().loadColumn()
		db.tables.each { tableToCsv(it,dir,batchSize,maxSize)}
	}

	private void tableToCsv(table,dir,batchSize,maxSize){
		for(int i=1;i<maxSize+1;i++){
			println "$table.TABLE_NAME : $i 번째 파일 처리중"
			def list = db.paging "select * from $table.TABLE_NAME ",batchSize,i
			if(list.size()==0) return;
			
			def fileCount = StringUtil.leftPad(i.toString(),4,'0')
			def fileForWrite = new File(dir , "${table.TABLE_NAME}#${fileCount}.csv")
			OpenCsv.writeMap(fileForWrite, list);
			
			if(list.size() < batchSize) return
		}
	}
	
	public fileToDb(dir,toDir,errorDir=null){
		if(dir instanceof String) dir = new File(dir)
		if(toDir instanceof String) toDir = new File(toDir)
		if(!toDir.exists()) toDir.mkdirs()
		dir.listFiles().each {
			try{
				def name = StringUtil.getFirst(it.name, '.');
				println "$name 시작"
				def list
				//if(it.name.toUpperCase().endsWith('.TXT')) list = new TextFile().readAsMap(it)
				if(it.name.toUpperCase().endsWith('.TXT')) list = null
				else if(it.name.toUpperCase().endsWith('.CSV')) list =  OpenCsv.readAsMap(it)
				else list =   PoiReaderFactory.instance(it)[0].read()
				println "$name 로드완료 : size = ${list.size()}"
				
				def tableName = StringUtil.getFirst(name, '#');
				eachFileToDb(tableName, list)
				assert it.renameTo(new File(toDir,it.name))
			}catch(e){
				if(errorDir==null) throw e
				else{
					System.err.out(e.message);
					if(errorDir instanceof String) errorDir = new File(errorDir)
					if(!errorDir.exists()) errorDir.mkdirs()
					assert it.renameTo(new File(errorDir,it.name))
				}
			}
		}
	}
	
	/** 테이즐정보를 로드해서 columnNames을 생략할 수 있게 해준다. Map안의 데이터중 컬럼과 매핑되는것만 insert한다.
	* ex) println db.insert('도메인정의서',PoiReaderFactory.instance(ROOT+'KMA_DA_도메인정의서_v1.72')[1].read()) \
	* 근데 이거 쓸일이 거의 없는듯. 망함. */
   public int eachFileToDb(TABLE_NAME,List listMap){
	   def columns = db.columns(TABLE_NAME)
	   if(columns.size()==0) throw new RuntimeException("테이블 $TABLE_NAME 를 찾을 수 없습니다.")
	   listMap.each { map ->
		   def mapForReplace = [:]
		   map.each{ key,value ->
			   if('PAGE_RN' == key) return //페이징 처리에 사용됨
			   def col = columns.find { it['COLUMN_NAME'] == key  }
			   if(col==null) throw new RuntimeException("자료의 $key 컬럼을 DB스키마에서 찾을 수 없습니다")
			   if(col['DATA_TYPE']=='DATE') mapForReplace[key] = new Timestamp( Long.valueOf(value) )
		   }
		   mapForReplace.each { k,v -> map[k] == v }
	   }
	   //columnsKey(TABLE_NAME,columns)
	   def columnNames  = columns.collect { it.COLUMN_NAME }
	   return db.insertListMap(TABLE_NAME,columnNames,listMap)
   }
   
   
   /** DB의 내용을 xls로 옮긴다. (임시용이다. 대용량은 txt로 처리하자)
   * 실DB의 일정분량만을 테스트DB로 이관할때 사용하였다.  batchSize * maxSize 가 전체수
   * ex) db.dbToXls ROOT, 500, 2, " and TABLE_NAME = 'AGR_AGRI_DAY' "*/
  @Deprecated
  public dbToXls(file,batchSize,maxSize,where = ''){
	  db.loadInfo(where).loadCount().loadColumn()
	  db.tables.each { tableToXls(file,batchSize,maxSize,it)}
  }

  @Deprecated
  private void tableToXls(file,batchSize,maxSize,table){
	  for(int i=1;i<maxSize+1;i++){
		  println "$table.TABLE_NAME : $i 번째 파일 처리중"
		  def list = paging "select * from $table.TABLE_NAME ",batchSize,i
		  if(list.size()==0) continue
			  Poi p = new Poi()
		  p.setListedMap table.TABLE_NAME, list
		  p.wrap()
		  def fileCount = StringUtil.leftPad(i.toString(),4,'0')
		  p.write new File(file + "/${table.TABLE_NAME}#${fileCount}.xls")
		  if(list.size() < batchSize) return
	  }
  }
  
  /** 컬럼 정보를 한 시트에 모두 담는다. */
  public dbInfo(fileName,where = ''){
	  Poi p = new Poi()
	  db.loadInfo(where).loadColumn()
	  db.tables.each { it['COUNT'] = count(it.TABLE_NAME) }
	  p.setListedMap("1.테이블목록",tables);
	  def allColums = []
	  int tableCount = 1
	  db.tables.each {
		  if(it.TABLE_NAME.startsWith('BIN')) return
			  def colums = it.columns
		  def conSql = """
			  SELECT aa.TABLE_NAME,COLUMN_NAME,CONSTRAINT_TYPE,SEARCH_CONDITION
			  FROM USER_CONS_COLUMNS aa join USER_CONSTRAINTS bb on aa.CONSTRAINT_NAME = bb.CONSTRAINT_NAME
			  WHERE aa.TABLE_NAME = '$it.TABLE_NAME' """
		  def con = list(conSql)
		  //제약조건들은 알아서 세팅
		  con.each{ c ->
			  colums.findAll { col-> c.COLUMN_NAME == col.COLUMN_NAME }.each { result->
				  if(c.CONSTRAINT_TYPE=='C') result['SEARCH_CONDITION'] = c.SEARCH_CONDITION
				  else  if(c.CONSTRAINT_TYPE=='P') result['KEY'] = 'PK'
			  }
		  }
		  p.addHyperlink tableCount++, 0, '2.컬럼정보', 'A', allColums.size()+2
		  allColums.addAll colums
	  }
	  p.setListedMap '2.컬럼정보',allColums
	  p.wrap()
	  p.getMerge(1).setAbleRow(0).setAbleCol(0).merge();
	  p.write(fileName)
  }
	
}
