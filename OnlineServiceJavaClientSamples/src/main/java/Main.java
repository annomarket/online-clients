import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;

import com.annomarket.online.AnnotatedDocument;
import com.annomarket.online.Annotation;
import com.annomarket.online.OnlineApi;
import com.annomarket.online.OnlineApiException;
import com.annomarket.online.SupportedMimeType;
import com.annomarket.shop.Item;
import com.annomarket.shop.Shop;


public class Main {

	private static String apiKey = "AMCic6omqxp1a";
	private static String apiPass = "j8v9tv4ef4y8t2vo6mnz";

	public static void main(String... args) throws IOException, OnlineApiException {

		//find the TwittIE shop item
		Shop s = new Shop(apiKey, apiPass);
		Item item = s.listItems("Twitter").get(0);

		//instantiate an API Object for anntating with this pipeline
		OnlineApi api = new OnlineApi(item, apiKey, apiPass);		
		System.out.println("Annotating documents with pipeline " + item.name + item.shortDescription);		

		//get all documents in a folder
		File f = new File("tweets");
		File[] documents = f.listFiles();

		//the output
		File results = new File("results");

		Writer w = null;
		try {
			w = new OutputStreamWriter(new FileOutputStream(results)); 

			for(File document : documents) {
				if(document.isDirectory() || !document.canRead()) {
					continue;
				}					
				//annotate each file
				AnnotatedDocument result = api.annotateFileContents(document, 
						Charset.forName("UTF-8"), 
						SupportedMimeType.TWITTER_JSON);

				String documentText = result.text;
				//get all annotations of a given type
				List<Annotation> annotationsOfType = result.entities.get("Location");
				if(annotationsOfType == null) continue;

				//append each annotation to the output
				for(Annotation annotation : annotationsOfType) {
					w.write(documentText.substring((int)annotation.startOffset, (int)annotation.endOffset));
					w.write("\n");
				}
			}
		} finally {
			w.close();
		}
	}
}
