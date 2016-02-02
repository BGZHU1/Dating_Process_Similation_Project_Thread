import java.util.Random;
import java.util.concurrent.Semaphore;

public class Smartpants extends Thread{
	public volatile int end_of_show;
	public int count;
	private static Random r=new Random();
	public static Contestant greetingContestant;
	public static long time = System.currentTimeMillis();

	public static Semaphore greeting_sm=new Semaphore (0,true);
	public static Semaphore arrived_sm=new Semaphore (0,true);
	public static Semaphore finish_grating_sm=new Semaphore (0,true);
	public static Semaphore increament_sm=new Semaphore (1,true);
	public static Semaphore brag_sm=new Semaphore (0,true);



	public Smartpants(){
		setName("SmartPants");
		msg(this);
	}

	public void run(){
		try {
			gratingContestant();
			contestant_tell_smpartpants_show_ends();
		} catch (InterruptedException e) {
			System.out.println("Opps gratings interrupts");
		}


	}


	public void gratingContestant() throws InterruptedException {
		
		
		for(int i=1; i<=Main.num_contestant;){
           //let the contestant to meet one by one if there is any
			
			
			greeting_sm.release();//mutex lock from contestant-let contestant enter date
			
        //make sure greeting contestant arrived by for talk
			arrived_sm.acquire();
			
			//talk with Contestant for random time
			int j=r.nextInt(1000);
			sleep(j);
			
            
			System.out.println("Hi "+" "+"SmartPant meets "+greetingContestant.getName());
            
			
	        //finish greeting one
			
			finish_grating_sm.release();

			//increment contestant count i
			increament_sm.acquire();  
			i++;
			increament_sm.release(); 
			
		


		}//for

		
	}

	private void contestant_tell_smpartpants_show_ends() throws InterruptedException {
		Date.show_is_over_sm.acquire();
	
		//print out anouncement
		System.out.println("\t"+"I, SmartPants anounces the show ends!");
		
		// then able to brag 
		brag_sm.release();
		

	}
	public void msg(Thread m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
}

