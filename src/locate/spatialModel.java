package locate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import KDE.model.Event;
import KDE.model.KDE;
import utility.Initialize;
import utility.geoCordinates;

public class spatialModel {

	double threshold_checkins;
	
	public spatialModel(double tc) throws ParseException{
		threshold_checkins = tc;
	}

	// train individual KDE model 
	public KDE trainSpatialModelIndividual(String user, Initialize init, int k) throws ParseException{

		List<Event> trainData = new ArrayList<Event>();
		ArrayList<SimpleEntry<String,String>> checkins = init.userCheckinList.get(user);
		if(checkins != null && checkins.size() > threshold_checkins){
			int i;
			for(i = 0 ; i< checkins.size() ;i++){
				SimpleEntry<geoCordinates,String[]> venueDetail = init.venue_info.get(checkins.get(i).getKey());
				trainData.add(new Event(checkins.get(i).getKey(), venueDetail.getKey().lat, venueDetail.getKey().lon));
			}
			KDE adaptive = KDE.trainAdaptiveKDE(trainData, k);
			return adaptive;
		}
		return null;
	}

    // train the global KDE model
	public KDE trainSpatialModelGlobal(Initialize init, int k) throws ParseException{
		List<Event> trainData = new ArrayList<Event>();
		for(Entry<String, ArrayList<SimpleEntry<String, String>>> checkins : init.userCheckinList.entrySet() ){
			if(checkins.getValue() != null && checkins.getValue().size() > threshold_checkins){
				for(int i = 0 ; i< checkins.getValue().size() ;i++){
					SimpleEntry<geoCordinates,String[]> venueDetail = init.venue_info.get(checkins.getValue().get(i).getKey());
					trainData.add(new Event(checkins.getValue().get(i).getKey(), venueDetail.getKey().lat, venueDetail.getKey().lon));
				}
			}
		}
		KDE adaptive = KDE.trainAdaptiveKDE(trainData, k);
		return adaptive;
	}
}
