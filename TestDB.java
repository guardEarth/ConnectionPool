import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class TestDB
{
	private static long start = System.currentTimeMillis();
	private static Thread thread;
	private static final String a = "jdbc:mysql://localhost/mysql";
	private static final String b = "root";
	private static final String c = "";
	private static final String d = "com.mysql.jdbc.Driver";
	private static final AtomicInteger exit = new AtomicInteger();
	private static final ConnectionPool cp = new ConnectionPool(50,a,b,c,d);
	
	public static void main(String[] args) throws SQLException, InterruptedException
	{
		thread = Thread.currentThread();
		start = System.currentTimeMillis();
		for(int i=0;i<1000;i++)
		{
			new Thread(new Runnable()
			{
			    public void run()
			    {
			    	Connection conn = null;
			    	for(int i=0;i<10000;i++)
					{
			    		try{
			    			conn = cp.getConnection();
		    			    conn.close();
		    		       }
		    		    catch (SQLException e) {e.printStackTrace();}
					}
			    	exit.incrementAndGet();
			    	if(exit.get() == 1000)
			    	{
			    		System.out.println("所用时间是"+(System.currentTimeMillis()-start)+"毫秒");
			    		exit.set(0);;
			    		LockSupport.unpark(thread);
			    	}
			    }
			}).start();
		}
		LockSupport.park();
		System.exit(0);
	}
}