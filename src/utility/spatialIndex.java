package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import utility.geoCordinates;

public class spatialIndex {
	
	
	
	public static RTree<String, Point> createIndex(String inputFile) throws IOException{
		
		RTree<String, Point> tree = RTree.star().create();
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		String line = br.readLine();
		while((line = br.readLine())!=null){
			String[] strList;
			strList=line.trim().split(",");
			//System.out.println(strList[0]);
			tree = tree.add(strList[0], Geometries.point(Float.parseFloat(strList[1]), Float.parseFloat(strList[2])));
			
		}
		return tree;
	}
	
	
	public static List<Entry<String, Point>> searchLocationsWithInRadius(RTree<String, Point> tree, geoCordinates venue, double distanceKm){
		
		Point searchPoint = Geometries.point(venue.lat,venue.lon);
		
		List<Entry<String, Point>> result = tree.search(searchPoint, 1).toList().toBlocking().single();;
		
		//for(Entry<String,Point> e : result)
		//System.out.println(e.value()+" "+e.geometry().x()+" "+e.geometry().y());
		return result;
	}
	
//	private static Rectangle createBounds(final Position from, final double distanceKm) {
//	        // this calculates a pretty accurate bounding box. Depending on the
//	        // performance you require you wouldn't have to be this accurate because
//	        // accuracy is enforced later
//	        Position north = from.predict(distanceKm, 0);
//	        Position south = from.predict(distanceKm, 180);
//	        Position east = from.predict(distanceKm, 90);
//	        Position west = from.predict(distanceKm, 270);
//	        
//	        return Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(), north.getLat());
//	    }
//	
//	public static <T> Observable<Entry<T, Point>> search(RTree<T, Point> tree, Point lonLat,
//            final double distanceKm) {
//        // First we need to calculate an enclosing lat long rectangle for this
//        // distance then we refine on the exact distance
//        final Position from = Position.create(lonLat.y(), lonLat.x());
//        Rectangle bounds = createBounds(from, distanceKm);
//
//        return tree
//                // do the first search using the bounds
//                .search(bounds)
//                // refine using the exact distance
//                .filter(new Func1<Entry<T, Point>, Boolean>() {
//                    public Boolean call(Entry<T, Point> entry) {
//                        Point p = entry.geometry();
//                        Position position = Position.create(p.y(), p.x());
//                        return from.getDistanceToKm(position) < distanceKm;
//                    }
//                });
//    }
	
	public static void main(String args[]) throws IOException{
		
		RTree<String,Point> tree = createIndex(args[0]);
		//System.out.println(tree.size());
		searchLocationsWithInRadius(tree, new geoCordinates(37.806167,-122.450135), 1);
	}

}
