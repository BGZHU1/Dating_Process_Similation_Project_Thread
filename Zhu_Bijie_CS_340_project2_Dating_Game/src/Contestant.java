import java.util.Random;
import java.util.concurrent.Semaphore;


public class Contestant extends Thread{

	private static Random r=new Random();
	public static long time = System.currentTimeMillis();
	public static int id;
	public volatile  static int contestant_doing;           
	public volatile static boolean  contestant_done=false;//share variable
	public volatile int rounds=0;  
	public volatile int count;
	
	
	


	//semaphores
	//semaphore for incrementation of rounds
	Semaphore rounds_sm=new Semaphore (1,true);
	//semaphore for avilable of date
	public static Semaphore dating_sm=new Semaphore (1,true);
	//for one contestant to meet a date at a time
	Semaphore contestant_meet_sm=new Semaphore (1,true);
	Semaphore contestant_doing_sm=new Semaphore (1,true);
	Semaphore contestant_done_sm=new Semaphore (1,true);
	public static Semaphore Date_is_Done_sm=new Semaphore (0,true);
	public static Semaphore leaving_in_group_sm=new Semaphore (1,true);
	public static Semaphore group_size_sm=new Semaphore (0,true);
	public static Semaphore one_at_a_time_for_contestant_sm=new Semaphore (1,true);
	


	public Contestant(int contestantID){
		id=contestantID;
		setName("Contestant-"+contestantID);
		msg(this);

	}



	public void run(){

		//list of all potential methods

		try {
			arrive_Club();
			//System.out.println(this.getName());
			meetSmartpant();
			meetDate();
			contestant_done();
			able_to_brag();
			brag();
			leave_in_group_size();
			



		} catch (InterruptedException e) {
			System.out.println("Opps,interrupted by meetDate");
		}


	}


	public  void arrive_Club() throws InterruptedException {
               //arrive at random time as sleep
		int i=r.nextInt(2000);
		//System.out.println("wait time"+i);
		sleep(i);
		System.out.println(this.getName()+" arrived club");

	}



	public void meetSmartpant() throws InterruptedException {
		
		
		//wait to meet Smartpants
		Smartpants.greeting_sm.acquire(); //mutex lock: once at a time
		//enter to meet Smartpants
		Smartpants.greetingContestant=this; //tell smartpants  which contestant to meeting
		
		//tell Smartpants he is arrived
		Smartpants.arrived_sm.release();
		
		//finish meet smartpants-able to start meet dates
		Smartpants.finish_grating_sm.acquire();

	}



	public void meetDate() throws InterruptedException {
	
		for(rounds=0;rounds<Main.num_rounds;){
			
	
			
		   //if no date aviliable - wait
		
			dating_sm.acquire();
	
			//now aviliable,entering to meet the date

			//tell the date which contestant she/he is meeting

			Date.cur_contestant=this;
			
			//after tell date who he is, he is able to wait 
			//for decision

			Date.decision_lock_sm.release();
			
			
			rounds_sm.acquire();
			rounds++;
			rounds_sm.release();
			
			
			


		}//inner-for
		



	}



	public void contestant_done() throws InterruptedException {

		contestant_doing_sm.acquire();
		contestant_doing++;
		contestant_doing_sm.release();
		

         
		if(contestant_doing==Main.num_contestant){
			contestant_done_sm.acquire();
			contestant_done=true;
			contestant_done_sm.release();

			//when last dates is done 
			Date_is_Done_sm.acquire();


		}	



	}

	public void able_to_brag() throws InterruptedException {
		Smartpants.brag_sm.acquire();
		
	}


	public void brag() throws InterruptedException{
		int i=r.nextInt(2000);
		sleep(i);
		Smartpants.brag_sm.release();//make sure no deadlock
		//the first release in smart pants only work once
		System.out.print(this.getName()+" finished bragging......"+"\n");
	}


	public void leave_in_group_size() throws InterruptedException {
		
       
		
		
		leaving_in_group_sm.acquire();//mutex
		Main.count_group_member++;
		//the release will be in two place- one for every n times, one for times that group size is not n
		
		
		

		if(Main.count_group_member%Main.group_size==0||Main.count_group_member==Main.num_contestant){
			//if it is group size or it is the last group member
			System.out.println("This group of "+Main.group_size+" is going home");
			leaving_in_group_sm.release();//mutex - for the one that is in size n
			//release group size-n times according to lecture notes - the last group just left
			while(count!=Main.group_size-1){
			group_size_sm.release();
			count++;//count how many times the release has been done
			
			}
			}
			
			
		else{
			leaving_in_group_sm.release();//mutex - for the one that not in size n
			group_size_sm.acquire();//make sure certain number is reached
		}

	}

	public void msg(Thread m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
}