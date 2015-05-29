/**
 * 
 */
package hackathon;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 */
public class FeedbackReader {
	private static final String[] Punctuations = {",",";","\\.","!","\\?"};
	private static final String[] sentimentChangers = {"not","could be","could have been","should be","should have been",
		"never","n't","no more","no way","no chance","no"};
	private static final String[] preJoiners = {"un","in","il","im","ir","non","mis","mal","dis","anti","de","under"};
	private static final String POSITIVE = "positive";
	private static final String NEGATIVE = "negative";
	private static final String NEUTRAL = "neutral";
	private static final String SPACE = " ";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "D:\\HackathonInput.txt";
		FileOutputStream fos = null;
		String line = null;
		try {
			FileReader fileReader = 
					new FileReader(fileName);
			BufferedReader bufferedReader = 
					new BufferedReader(fileReader);
			String[] subSentence = new String[20];
			boolean soloSentence = true;
			String trimmedPunctuation = null;
			File outputFile = new File("D:\\PQRockers_Out.txt");
			//fos = new FileOutputStream(outputFile);
			// if file doesnt exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			while((line = bufferedReader.readLine()) != null) {
				if( line.length() <= 1){
					//System.out.println("empty line");
					continue;
				}
				List<String> feedbacks = new ArrayList<String>();
				for(String punctuation: Punctuations){
					if(punctuation.length() > 1){
						trimmedPunctuation = Character.toString(punctuation.charAt(punctuation.length()-1));
					}
					else{
						trimmedPunctuation = punctuation;
					}
					if(line.contains(trimmedPunctuation)){
						soloSentence = false;
						subSentence = line.split(punctuation);
						for(String sentence: subSentence){
							if(sentence.contains(SPACE) && sentence.trim().length() > 1){
								String feedback = determineFeedbackNature(sentence);
								feedbacks.add(feedback);
							}
						}
					}
				}
				if(soloSentence){
					String feedback1 = determineFeedbackNature(line);
					feedbacks.add(feedback1);
				}
				if(feedbacks.contains(NEGATIVE)){
					System.out.println("Sentence is: " + line);
					System.out.println("Final feedback is: " + NEGATIVE);
					bw.append(NEGATIVE);
					bw.newLine();
				}
				else if(feedbacks.contains(POSITIVE)){
					System.out.println("Sentence is: " + line);
					System.out.println("feedback: " + POSITIVE);
					bw.append(POSITIVE);
					bw.newLine();
				}
				else{
					System.out.println("Sentence is: " + line);
					System.out.println("feedback: " + NEUTRAL);
					bw.append(NEUTRAL);
					bw.newLine();
				}
				soloSentence = true;
			}    
			Desktop.getDesktop().open(outputFile);
			bufferedReader.close();
			bw.close();
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ fileName + "'");                   
		}catch(Exception e){
			System.out.println("Exception occured: " + e);
		}
	}

	public static String determineFeedbackNature(String sentence){
		String positiveFile = "C:\\Users\\Administrator\\hackathonWS\\FeedBackAnalyzer\\src\\hackathon\\Positives.txt";
		String negativeFile = "C:\\Users\\Administrator\\hackathonWS\\FeedBackAnalyzer\\src\\hackathon\\Negatives.txt";
		String result = NEUTRAL;
		try{
			FileReader fileReader = new FileReader(positiveFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String positiveSentiments = bufferedReader.readLine();
			String[] positiveCommets = positiveSentiments.split(",");
			for(String comment: positiveCommets){
				comment = comment.trim();
				if(sentence.toLowerCase().contains(comment.toLowerCase())){
					for(String sentiChanger: sentimentChangers){
						Pattern p = Pattern.compile("("+sentiChanger+")(.*)("+comment+")");
						Matcher m = p.matcher(sentence);
						if(m.find()){
							result = NEGATIVE;
							break;
						}
					}
					for(String prefix: preJoiners){
						Pattern p = Pattern.compile("(?<="+prefix+")(.*)(?="+comment+")");
						Matcher m = p.matcher(sentence);
						if(m.find()){
							if(m.group().toString().length() == 0){
								if(!(sentence.toLowerCase().contains("incredible") ||  sentence.toLowerCase().contains("unbelievable") || sentence.toLowerCase().contains("unlimited"))){
									result = NEGATIVE;
									break;
								}
							}
						}
					}
					if(result.equals(NEUTRAL)){
						result = POSITIVE;
					}
				}
			}
			FileReader fileReader1 = new FileReader(negativeFile);
			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
			String negativeSentiments = bufferedReader1.readLine();
			String[] negativeCommets = negativeSentiments.split(",");
			for(String comment: negativeCommets){
				if(sentence.toLowerCase().contains(comment.toLowerCase())){
					result = NEGATIVE;
					for(String sentiChanger: sentimentChangers){
						Pattern p = Pattern.compile("(" + sentiChanger + ") .* (" + comment + ")");
						Matcher m = p.matcher(sentence);
						if(m.find()){
							result = POSITIVE;
							break;
						}
					}
					if(result.equals(NEUTRAL)){
						result = NEGATIVE;
						break;
					}
				}
			}
			bufferedReader.close();
			bufferedReader1.close();
			return result;
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							positiveFile + "'");
			return null;
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ positiveFile + "'");
			return null;
		}catch(Exception e){
			System.out.println("Exception occured in determining feedback: " + e);
			return null;
		}
	}

}
