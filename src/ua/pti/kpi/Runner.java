package ua.pti.kpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

public class Runner {

	public static final String DICTIONARY_PATH = "/home/acersanya/Documents/Intelecual/stopwords_dictionary.txt";
	public static final String TEXT_PATH = "/home/acersanya/Documents/Intelecual/text.txt";
	public static final String OUTPUT_FILE = "/home/acersanya/Documents/Intelecual/output.txt";
	public static String[] dictionary = ReadDictionary(DICTIONARY_PATH).toString().split(" ");

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static StringBuilder Reader(String path) {

		StringBuilder builder = null;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String sCurrentLine;
			builder = new StringBuilder();

			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder;
	}

	public static StringBuilder ReadDictionary(String path) {

		StringBuilder builder = null;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String sCurrentLine;
			builder = new StringBuilder();

			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine + " ");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder;
	}

	public static ArrayList<String> threeGram(String input) {

		ArrayList<String> list = new ArrayList<>();
		String[] split = input.split(" ");
		StringBuilder temp = null;
		for (int i = 0; i < split.length - 2; i++) {
			temp = new StringBuilder();
			temp.append(split[i] + " ");
			temp.append(split[i + 1] + " ");
			temp.append(split[i + 2]);
			if (improveStopDictionary(dictionary, temp.toString())) {
				list.add(temp.toString());
			}
		}
		return list;
	}

	public static ArrayList<String> bigram(String input) {

		ArrayList<String> list = new ArrayList<>();
		String[] split = input.split(" ");
		StringBuilder temp = null;
		for (int i = 0; i < split.length - 1; i++) {
			temp = new StringBuilder();
			temp.append(split[i] + " ");
			temp.append(split[i + 1]);
			if (improveStopDictionaryBigram(dictionary, temp.toString())) {
				list.add(temp.toString());
			}
		}
		return list;
	}

	public static ArrayList<String> monogram(String input) {

		ArrayList<String> list = new ArrayList<>();
		String[] split = input.split(" ");
		StringBuilder temp = null;
		for (int i = 0; i < split.length; i++) {
			temp = new StringBuilder();
			temp.append(split[i]);
			if (stopDictMono(dictionary, temp.toString())) {
				list.add(temp.toString());
			}
		}
		return list;
	}

	
	
	public static void groupThreeGramms(ArrayList<String> threeGramms) {
		Collections.sort(threeGramms, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
	}

	public static String formater(StringBuilder input) {
		String temp = input.toString().toLowerCase().trim().replaceAll("\\s+", " ");
		temp = temp.replaceAll("[^A-Za-z]+", " ");
		return temp;
	}

	public static String useStopDictionary(String[] dictionary, String text) {
		String[] textArray = text.split(" ");
		StringBuilder result = new StringBuilder();
		for (String i : textArray) {
			boolean checker = true;
			for (String j : dictionary) {
				if (i.equals(j)) {
					checker = false;
					break;
				}
			}
			if (checker == true) {
				result.append(i + " ");
			}
		}
		return result.toString();
	}

	public static boolean stopDictMono(String[] dictionary, String text) {
		ArrayList<String> tempList = new ArrayList<>();
		for (String i : dictionary) {
			tempList.add(i);
		}

		if (tempList.contains(text)) {
			return false;
		}
		return true;

	}

	public static boolean improveStopDictionaryBigram(String[] dictionary, String text) {

		String[] temp = text.split(" ");

		ArrayList<String> tempList = new ArrayList<>();
		for (String i : dictionary) {
			tempList.add(i);
		}

		if (tempList.contains(temp[0]) || (tempList.contains(temp[1]))) {
			return false;
		} else {
			return true;
		}

	}

	public static boolean improveStopDictionary(String[] dictionary, String text) {

		String[] temp = text.split(" ");

		ArrayList<String> tempList = new ArrayList<>();
		for (String i : dictionary) {
			tempList.add(i);
		}

		if (tempList.contains(temp[0]) || (tempList.contains(temp[2]))) {
			return false;
		}
		return true;
	}

	public static ArrayList<String> splitOnParts(String text) {
		Iterable<String> chunks = Splitter.fixedLength(200).split(text);
		ArrayList<String> temp = com.google.common.collect.Lists.newArrayList(chunks);
		return temp;
	}

	public static Map<String, Double> weight(ArrayList<String> list) {

		Map<String, Double> hm = new HashMap<>();

		groupThreeGramms(list);

		LinkedHashSet<String> set = new LinkedHashSet<>();
		ArrayList<String> uniqueList = new ArrayList<>();
		set.addAll(list);
		uniqueList.addAll(set);
		double[] weigth = new double[set.size()];
		String temp = list.get(0);
		int counter = 0;
		int j = 0;
		for (int i = 0; i < list.size(); i++) {
			if (temp.equals(list.get(i))) {
				counter++;
			} else {
				temp = list.get(i);
				weigth[j] = (double) counter / list.size();
				// storing UNIQUE KEY AND VALUE
				hm.put(uniqueList.get(j), new Double(weigth[j]));
				j++;
				counter = 1;
			}
		}
		hm.put(uniqueList.get(j), new Double((double) counter) / list.size());

		return hm;
	}

	public static void computeAll(String path) {

		String result = formater(Reader(path));

		// Split on parts our text
		ArrayList<String> documentParts = splitOnParts(result);

		// TF
		ArrayList<Map<String, Double>> weighArray = new ArrayList<>();
		ArrayList<Map<String, Double>> TFIDF = new ArrayList<>();
		for (int i = 0; i < documentParts.size(); i++) {
			weighArray.add(weight(threeGram(documentParts.get(i))));
		}

		writer(weighArray);

		// TF-IDF

		for (int i = 0; i < weighArray.size(); i++) {
			for (Map.Entry<String, Double> entry : weighArray.get(i).entrySet()) {
				int counter = 0;
				for (int j = 0; j < weighArray.size(); j++) {
					if (i == j) {
						continue;
					}
					for (Map.Entry<String, Double> entrySecond : weighArray.get(j).entrySet()) {
						if (entry.getKey().equals(entrySecond)) {
							counter++;
							break;
						}
					}
				}
				if (counter == 0) {
					entry.setValue(new Double(0));
				} else {
					Double temp = entry.getValue() * (Math.log(weighArray.size() / counter));
					entry.setValue(temp);
				}
			}
		}
	}

	public static void writer(ArrayList<Map<String, Double>> input) {

		Map<String, Double> general = new HashMap<>();

		for (Map<String, Double> i : input) {
			for (Map.Entry e : i.entrySet())
				if (!general.containsKey(e.getKey())) {
					general.put((String) e.getKey(), (Double) e.getValue());
				}
		}

		LinkedHashMap<String, Double> s = general.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		try {
			File fileOne = new File(OUTPUT_FILE);
			FileOutputStream fos = new FileOutputStream(fileOne);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			for (Map.Entry<String, Double> entry : s.entrySet()) {
				oos.writeObject(entry.toString() + "\n");

			}
			oos.flush();
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		computeAll(TEXT_PATH);
	}

}
