import java.util.Random;
import java.util.concurrent.Semaphore;


public class Date extends Thread{
	private static Random r=new Random();
	int i=r.nextInt(2000);
	public static long time = System.currentTimeMillis();
	public static Contestant cur_contestant;
	public volatile int ave_count=0;
	
	public static Semaphore dateCount_sm=new Semaphore (1,true);
	public static Semaphore decision_lock_sm=new Semaphore (0,true);
	public static Semaphore decision_lock2_sm=new Semaphore (0,true);
	public static Semaphore last_date_done_sm=new Semaphore (1,true);
	public static  Semaphore show_is_over_sm=new Semaphore (0,true);
	public static  Semaphore date_ave_sm=new Semaphore (0,true);
	public static Semaphore ave_count_sm=new Semaphore (1,true);
	public static Semaphore one_at_a_time_sm=new Semaphore (1,true);

	public Date (int j){

		setName("Date"+j);

		msg(this);

	}

	public void run(){

		System.out.println(this.getName()+"start to run");
		try {
			Date_Approached_Contestants();

		} catch (InterruptedException e) {

		}
		//System.out.print(this.getName()+"returned from date");

	}

	public void Date_Approached_Contestants() throws InterruptedException  {

		while(!Contestant.contestant_done){
			//a date can only talk to one contestant at a time
		
			one_at_a_time_sm.acquire();
			
			ave_count_sm.acquire();
			
			//release the date if there is aviliable one
			if(ave_count<Main.num_date){
				ave_count++;
				
			Contestant.dating_sm.release();
		
			//make sure date know who she is talking with
			decision_lock_sm.acquire();
			decison();
    
			//release the decision
			
			//after making decision date, release the date as aviliable

			Contestant.dating_sm.release();
		
			
		
			
			
			ave_count_sm.release();
			}
			else{
				//wait for decision release
				decision_lock2_sm.acquire();
				ave_count--;
				ave_count_sm.release();
				
			}
			one_at_a_time_sm.release();
			
			

		}//while
		//after finish one round; increase one; after finish n rounds, done
		Last_Date_Done_Release();
		
		//System.out.println("I am break the while loop");

	}	



	public void decison() throws InterruptedException {


		//try to make decision:increase priority to max

		this.setPriority(MAX_PRIORITY);
		//talk
		sleep(i);
		//decide give number or not
		Random random = new Random();
		//whether or not give out number


		if(random.nextBoolean()){
			
           
			System.out.println("Yeh! The "+ cur_contestant.getName()+" get the"+ this.getName()+"'s Phone number" );
		}


		else{
			//if(cur_contestant==null){return;}
			System.out.println("Sorry! The"+ cur_contestant.getName()+" did not get "+ this.getName()+"'s Phone number" );
		}

        //release one date if date is full
		if(ave_count>=Main.num_date){
		decision_lock2_sm.acquire();
		}
		//decrease priority after making decision
		Thread.currentThread().setPriority(NORM_PRIORITY);

	}


	public void Last_Date_Done_Release() throws InterruptedException {


		last_date_done_sm.acquire();

		Main.count_date++;

		last_date_done_sm.release();  

		//tell contestant the dates is done
		if(Main.count_date==Main.num_date){
			
			Contestant.Date_is_Done_sm.release();
			
			//tell smartpants all dates all done dating
			
			show_is_over_sm.release();
		}

	}





	public void msg(Thread m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
}
