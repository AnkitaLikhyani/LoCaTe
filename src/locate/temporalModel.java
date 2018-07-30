package locate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.math.*;

import utility.Initialize;
import utility.geoCordinates;
import utility.utility;

public class temporalModel {

	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	Map<Double, Double> timeLagModel = new HashMap<Double, Double>();
	Map<String, Double> minTimeLag = new HashMap<String, Double>();
	double lambda, threshold_checkins;

	public temporalModel(double tc) {
		threshold_checkins = tc;
	}

	public void trainDecayFunction(Initialize init) throws ParseException, IOException {

		// File file = new File("temporal_result");
		// FileWriter fw = new FileWriter(file);

		for (Entry<String, ArrayList<SimpleEntry<String, String>>> e_user : init.userCheckinList.entrySet()) {
			if (e_user.getValue() != null && init.social_graph.get(e_user.getKey()) != null) {
				for (String e_friend_list : init.social_graph.get(e_user.getKey())) {
					if (init.userCheckinList.get(e_friend_list) != null)
						checkinsIntersection(e_user.getValue(), e_friend_list, init);
				}
			}
		}

		double mean = 0.0, count = 0.0;

		for (Entry<Double, Double> e : timeLagModel.entrySet()) {
			// System.out.println(e.getKey()+"\t"+ e.getValue());
			mean = mean + (e.getValue() * e.getKey());
			count = count + e.getValue();
		}
		mean = mean / count;
		lambda = 1.0 / mean;
		System.out.println("lambda : " + lambda + " mean : " + mean);
		// fw.flush();
		// fw.close();
	}

	public void checkinsIntersection(ArrayList<SimpleEntry<String, String>> user1_checkin, String user2,
			Initialize init) throws ParseException, IOException {

		ArrayList<SimpleEntry<String, String>> user2_checkin = init.userCheckinList.get(user2);

		for (int i = 0; i < user1_checkin.size(); i++) {
			for (int j = 0; j < user2_checkin.size(); j++) {
				if (user2_checkin.get(j).getKey().equals(user1_checkin.get(i).getKey())) {
					Date date_user1 = formatter.parse(user1_checkin.get(i).getValue());
					Date date_user2 = formatter.parse(user2_checkin.get(j).getValue());
					if (date_user2.getTime() > date_user1.getTime()) {
						double timeLag = utility
								.truncateTo((date_user2.getTime() - date_user1.getTime()) / (1000 * 60 * 60 * 24));
						// fw.write(String.valueOf(timeLag));
						// fw.write("\n");
						// System.out.println("user 1
						// :"+user1_checkin.get(i).getValue());
						// System.out.println("user 2
						// :"+user2_checkin.get(j).getValue());
						// if(timeLag < 0)
						// System.out.println("timelag : "+timeLag);
						if (minTimeLag.containsKey(user2) && minTimeLag.get(user2) > timeLag)
							minTimeLag.put(user2, timeLag);
						else
							minTimeLag.put(user2, timeLag);

						if (timeLagModel.containsKey(timeLag)) {
							// System.out.println("before :
							// "+timeLagModel.size());
							timeLagModel.put(timeLag, timeLagModel.get(timeLag) + 1.0);
							// System.out.println("after :
							// "+timeLagModel.size());
						} else
							timeLagModel.put(timeLag, 1.0);
					}
				}
			}
		}

	}

	public void checkinsIntersection(ArrayList<SimpleEntry<String, String>> user1_checkin, String user2,
			Initialize init, FileWriter fw) throws ParseException, IOException {

		ArrayList<SimpleEntry<String, String>> user2_checkin = init.userCheckinList.get(user2);

		for (int i = 0; i < user1_checkin.size(); i++) {
			for (int j = 0; j < user2_checkin.size(); j++) {
				if (user2_checkin.get(j).getKey().equals(user1_checkin.get(i).getKey())) {
					Date date_user1 = formatter.parse(user1_checkin.get(i).getValue());
					Date date_user2 = formatter.parse(user2_checkin.get(j).getValue());
					if (date_user2.getTime() > date_user1.getTime()) {
						double timeLag = utility
								.truncateTo((date_user2.getTime() - date_user1.getTime()) / (1000 * 60 * 60 * 24));

						fw.write(String.valueOf(timeLag));
						fw.write("\n");
						// System.out.println("user 1
						// :"+user1_checkin.get(i).getValue());
						// System.out.println("user 2
						// :"+user2_checkin.get(j).getValue());
						// if(timeLag < 0)
						// System.out.println("timelag : "+timeLag);
						if (minTimeLag.containsKey(user2) && minTimeLag.get(user2) > timeLag)
							minTimeLag.put(user2, timeLag);
						else
							minTimeLag.put(user2, timeLag);

						if (timeLagModel.containsKey(timeLag)) {
							// System.out.println("before :
							// "+timeLagModel.size());
							timeLagModel.put(timeLag, timeLagModel.get(timeLag) + 1.0);
							// System.out.println("after :
							// "+timeLagModel.size());
						} else
							timeLagModel.put(timeLag, 1.0);
					}
				}
			}
		}
	}

	public double getTMProb(String user) throws ParseException {
		double t0 = 0.0;
		if (minTimeLag.get(user) != null)
			t0 = (double) minTimeLag.get(user);
		// System.out.println("t0 :"+t0);
		// System.out.println("lambda : "+lambda);
		return lambda / (Math.pow(Math.E, (lambda * t0)));

	}

}
