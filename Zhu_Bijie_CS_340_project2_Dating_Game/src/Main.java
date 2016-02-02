

public class Main {


	static int num_contestant = 10; 
	static int num_date = 6; 
	static int num_rounds = 3; 
	static int group_size=3;
	static long time = System.currentTimeMillis();
	public static int count_date=0;
	public static int count_group_member=0;

	public static void main(String[] args) {
		if (args.length == 3) {
			num_date = Integer.parseInt(args[0]);
			num_rounds = Math.min(num_date, Integer.parseInt(args[1]));
			num_contestant = Integer.parseInt(args[2]);
		}


		//create certain number of contestant
		for (int j = 1; j <= num_contestant; j++){

			Contestant contestant=new Contestant(j);
			contestant.start();


		}
		//create smartPants
		Smartpants smartPants=new Smartpants();
		smartPants.start();		



		for (int j = 1; j <= num_date; j++){
			Date date=new Date(j);
			date.start();

		}

	}
}


