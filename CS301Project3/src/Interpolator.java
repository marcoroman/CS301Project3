import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interpolator {
	public static void main(String[] args) throws FileNotFoundException{
		File inputFile = new File("input.txt");
		File outputFile = new File("output.txt");
		
		PrintWriter writer = new PrintWriter(outputFile);
		Scanner reader = new Scanner(inputFile);
		
		ArrayList<Float> data = new ArrayList<>();
		
		//Reading all values from input file
		while(reader.hasNext())
			data.add(reader.nextFloat());
		
		reader.close();
		
		ArrayList<Float> xValues = new ArrayList<>();
		ArrayList<Float> functionValues = new ArrayList<>();
		
		//Splitting values of x from corresponding functional values
		xValues.addAll(0, data.subList(0, data.size() / 2));
		functionValues.addAll(0, data.subList(data.size() / 2, data.size()));
		
		ArrayList<ArrayList<Float>> dividedDifferenceTable = new ArrayList<>();
		//dividedDifferenceTable.add(new ArrayList<Float>());
		dividedDifferenceTable = createDividedDifferenceTable(xValues, functionValues);
		
		for(int i = 0; i < dividedDifferenceTable.size(); ++i){
			System.out.println(Arrays.toString(dividedDifferenceTable.get(i).toArray()));
		}
	}
	
	public static ArrayList<ArrayList<Float>> createDividedDifferenceTable(ArrayList<Float> x, ArrayList<Float> fx){
		ArrayList<ArrayList<Float>> table = new ArrayList<>();
		table.add(fx);
		
		for(int i = 0; i < x.size() - 1; ++i){
			table.add(new ArrayList<Float>());
			
			for(int j = 0; j < x.size() - (i + 1); ++j){
				table.get(table.size() - 1).add((table.get(i).get(j + 1) - table.get(i).get(j)) / (x.get(j + (i + 1)) - x.get(j)));
			}
		}
		
		return table;
	}
}