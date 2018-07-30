package locate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.*;
import java.io.*;


import utility.Initialize;
import utility.geoCordinates;
import utility.utility;

public class categoryTopicModel {
	
	public int numTopics;
	public Map<String,Map<Integer, Double>> category_topics = new HashMap<String,Map<Integer, Double>>();
	public double[] topicDistribution;
	public double[] topicWeightSum;
	public Map<String, Integer> avgTopicsPerUser = new HashMap<String, Integer>();
	public double threshold_checkins;
	
	public categoryTopicModel(int topics, double tc) {
		threshold_checkins = tc;
		numTopics = topics;
		topicDistribution = new double[topics];
		topicWeightSum = new double[topics];
	}

	public InstanceList categoryStream (Initialize init, ArrayList<Pipe> pipeList){
		
		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		
		for(Entry<String, ArrayList<SimpleEntry<String, String>>> e : init.userCheckinList.entrySet()){
			String categoriesVisited = "";
			for(int i = 0 ; i< e.getValue().size() ;i++){
				SimpleEntry<geoCordinates,String[]> venueDetail = init.venue_info.get(e.getValue().get(i).getKey());
				if(venueDetail.getValue() != null){
					for(int j = 0; j< venueDetail.getValue().length;j++){
						if(!venueDetail.getValue()[j].equals("")){
							String match_string = venueDetail.getValue()[j].replaceAll(" ", "_");
							match_string = match_string.replaceAll("\\(", "");
							match_string = match_string.replaceAll("\\)", "");
							match_string = match_string.replaceAll("\\/", "");
							categoriesVisited = categoriesVisited + " " + match_string ;
						}
					}
				}
			}
			CharSequence cs = categoriesVisited;
			Instance inst = new Instance(cs, e.getKey(), e.getKey(), e.getKey());
//			System.out.println(inst.getData());
//			System.out.println();
			instances.addThruPipe(inst);
		}
		
		return instances;
	}
	
	
	public Entry<Double,Map<Integer,Double>> categoryModel(String user, Initialize init){
		ArrayList<SimpleEntry<String, String>> checkins = init.userCheckinList.get(user);
		
		Map<Integer,Double> user_topic_dist = new HashMap<Integer, Double>();
		double topicsVisited = 0.0;
		if(checkins != null && checkins.size() > threshold_checkins){
			
			for(int i = 0 ; i< checkins.size() ;i++){
				SimpleEntry<geoCordinates,String[]> venueDetail = init.venue_info.get(checkins.get(i).getKey());
				if(venueDetail.getValue() != null){
				for(int c = 0; c < venueDetail.getValue().length ; c++){
					
					String match_string = venueDetail.getValue()[c].replaceAll(" ", "_");
					match_string = match_string.replaceAll("\\(", "");
					match_string = match_string.replaceAll("\\)", "");
					match_string = match_string.replaceAll("\\/", "");
					match_string = match_string.toLowerCase();
					Map<Integer, Double> topicDist = category_topics.get(match_string);
					
					if(topicDist != null){
						for(Entry<Integer, Double> e : topicDist.entrySet()){
							topicsVisited = topicsVisited + 1.0;
							if(user_topic_dist.containsKey(e.getKey()))
								user_topic_dist.put(e.getKey(), user_topic_dist.get(e.getKey()) + 1.0);
							else
								user_topic_dist.put(e.getKey(), 1.0);
						}
					}	
				}
				}
			}
		}
//		for(Entry<Integer, Double> e : user_topic_dist.entrySet())
//			System.out.println("topic : "+e.getKey()+" count : "+e.getValue());
//		System.out.println("topics visited : "+topicsVisited);
		avgTopicsPerUser.put(user, user_topic_dist.size());
		return new SimpleEntry<Double,Map<Integer,Double>>(topicsVisited, user_topic_dist);
	}
	
	
	
	

	
	private double getCProb(Entry<Double,Map<String,Double>> user_category_dist, String[] categories){
		double prob = 0.0;
		for(int c = 0 ; c< categories.length ;c++ ){
			if(user_category_dist.getValue().get(categories[c]) != null)
				prob = prob * user_category_dist.getValue().get(categories[c])/user_category_dist.getKey();	
		}
		return prob;
	}
	
	public double getCTMProb(Entry<Double,Map<Integer,Double>> user_topic_dist, String[] categories) {
		double prob = 0.0;
		for(int c = 0 ; c< categories.length ;c++ ){
			String match_string = categories[c].replaceAll(" ", "_");
			match_string = match_string.replaceAll("\\(", "");
			match_string = match_string.replaceAll("\\)", "");
			match_string = match_string.replaceAll("\\/", "");
			match_string = match_string.toLowerCase();
			Map<Integer, Double> category_topic_dist = category_topics.get(match_string);
			//System.out.println(category_topic_dist);
			if(category_topic_dist != null){
				for(Entry<Integer, Double> e_cat_dist : category_topic_dist.entrySet()){
//					System.out.println("P(T|c) : "+(e_cat_dist.getValue()/topicWeightSum[e_cat_dist.getKey()]));
//                  System.out.println("Pu(u|T) : "+((user_topic_dist.getValue().get(e_cat_dist.getKey()) / user_topic_dist.getKey()) + topicDistribution[e_cat_dist.getKey()]));
//					System.out.println("Pg(u|T) : "+topicDistribution[e_cat_dist.getKey()]);
					if(user_topic_dist.getValue().get(e_cat_dist.getKey()) != null && user_topic_dist.getKey() != null)
						prob = prob + (e_cat_dist.getValue()/topicWeightSum[e_cat_dist.getKey()]) * ((user_topic_dist.getValue().get(e_cat_dist.getKey()) / user_topic_dist.getKey()) + topicDistribution[e_cat_dist.getKey()]) ; 
					else
						prob = prob + (e_cat_dist.getValue()/topicWeightSum[e_cat_dist.getKey()]) * (topicDistribution[e_cat_dist.getKey()]);
				}
			}
		}
		return prob;
	}

	public void topicModel (Initialize init) throws IOException{
		
		// Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
//      pipeList.add( new TokenSequenceRemoveStopwords(new File(stopListFile), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        // Create a model with number of topics, alpha_t = 1.0, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        
        InstanceList instances = categoryStream(init, pipeList);
        
 //       System.out.println(instances.size());
//        System.out.println(instances.get(0).getData());
        
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop
        
        model.setNumIterations(50);
        model.estimate();

        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        
        // Estimate the topic distribution of the first instance, 
        //  given the current Gibbs state.
        topicDistribution = model.getTopicProbabilities(0);


        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        sortTopicsCategoryWise(topicSortedWords, dataAlphabet);
        double avg_topics = 0.0;
        for(int topic = 0; topic < numTopics; topic++){
        	avg_topics = avg_topics + topicSortedWords.get(topic).size();
        }
       System.out.println("avg number of categories per topic : "+(avg_topics/(double)numTopics));
        
	}
	
	
	private void sortTopicsCategoryWise(
			ArrayList<TreeSet<IDSorter>> topicSortedWords, Alphabet dataAlphabet) {
		
		for (int topic = 0; topic < numTopics; topic++) {
          Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
          double weight_sum = 0.0;
          while (iterator.hasNext()) {
              IDSorter idCountPair = iterator.next();
              weight_sum = weight_sum + idCountPair.getWeight();
              String category = dataAlphabet.lookupObject(idCountPair.getID()).toString();
              if(category_topics.containsKey(category)){
            	  category_topics.get(category).put(topic, idCountPair.getWeight());
              }else{
            	  Map<Integer, Double> tmp = new HashMap<Integer, Double>();
            	  tmp.put(topic, idCountPair.getWeight());
            	  category_topics.put(category, tmp);
              }
          }
          topicWeightSum[topic] = weight_sum;
      }
//		for(Entry<String, Map<Integer, Double>> e: category_topics.entrySet())
//			System.out.println(e);
		
	}
}
