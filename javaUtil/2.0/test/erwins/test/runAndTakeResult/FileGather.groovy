package erwins.test.runAndTakeResult


/** 그루비~ 굿 */
public class FileGather{
    
    /** root폴더트리 내의 jsp파일을 모두 root폴더로 옮긴다. */
    //@Test
    public void renameTo(){
        File root = new File("H:/cg");
        println root.path
        root.eachFileRecurse{if(it.file && it.name.endsWith('jpg') && it.length() > 10 ){
                File temp = new File(root.path + '/' + it.name);
                int i = 0;
                while(temp.exists()){
                    String[] qq = StringUtil.getExtentions(temp.getAbsolutePath());
                    temp = new File(qq[0] + '[' + ++i + '].' + qq[1] );
                }
                println temp.absolutePath;
                println it.renameTo(temp);
            };
        }
    }
    
    /** 폴더 이름으로 텍스트 파일 합치기 */
    /*
    @Test
    public void gatherText(){
        File root = new File("D:/테스트데이터_0217/mssql");
        StringBuilder b = new StringBuilder();
        root.eachFileRecurse{if(it.file && it.name.endsWith('txt')){
        		it.eachLine{println it};
            };
        }
    } */   
    
    /** 작은 파일 삭제. */
    //@Test
    public void delete(){
        File root = new File("H:/cg");
        println root.path
        root.eachFileRecurse{if(it.file && it.name.endsWith('jpg') && it.length() < 5000){
                it.delete();
            };
        }
    }
    
}

