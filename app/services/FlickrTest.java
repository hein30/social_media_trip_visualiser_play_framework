package services;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.google.common.collect.Sets;

/**
 * Created by Hein Min Htike on 7/11/2017.
 */
public class FlickrTest {

  public static void main(String args[]) throws FlickrException {
    Flickr flickr = new Flickr("b0cecbb71ec3c356567fe8af4fdba3fc", "fb4f4806ffe4abf0", new REST());
    // final SearchParameters params = new SearchParameters();
    // params.setBBox("-0.3515", "51.3849", "0.1483", "51.6723");
    // final PhotosInterface photosInterface = flickr.getPhotosInterface();
    // PhotoList<Photo> photos = photosInterface.search(params, 10000000, 1);

    final PeopleInterface peopleInterface = flickr.getPeopleInterface();
    PhotoList<Photo> photos = peopleInterface.getPhotos("12608538@N03", null, null, null, null,
        null, null, null, Sets.newHashSet("date_taken", "geo"), 1000, 1);
    System.out.println("");
  }
}
