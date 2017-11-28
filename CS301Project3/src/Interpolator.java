import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interpolator {
	public static void main(String[] args) throws FileNotFoundException{
		
		ArrayList<ArrayList<Float>> dividedDifferenceTable = new ArrayList<>();
		
		//Generating the divided differences based on input data
		createDividedDifferenceTable(dividedDifferenceTable);

		display(dividedDifferenceTable);
	}
	
	//Divided difference table stored as an ArrayList of ArrayLists
	//Generated based on provided input file
	public static void createDividedDifferenceTable(ArrayList<ArrayList<Float>> table) throws FileNotFoundException{
		
		File inputFile = new File("input.txt");
		Scanner reader = new Scanner(inputFile);
		
		ArrayList<Float> data = new ArrayList<>();
		
		//Reading all values from input file
		while(reader.hasNext())
			data.add(reader.nextFloat());
		
		reader.close();
		
		//First ArrayList in the table stores x values
		table.add(new ArrayList<Float>());
		table.get(0).addAll(0, data.subList(0, data.size() / 2));
				
		//Second ArrayList in the table stores functional values
		table.add(new ArrayList<Float>());
		table.get(1).addAll(0, data.subList(data.size() / 2, data.size()));
		
		//Iteratively filling in the table
		for(int i = 0; i < table.get(0).size() - 1; ++i){
			table.add(new ArrayList<Float>());
			
			//Divided difference calculation
			for(int j = 0; j < table.get(0).size() - (i + 1); ++j)
				table.get(table.size() - 1).add((table.get(i + 1).get(j + 1) - table.get(i + 1).get(j)) / 
						(table.get(0).get(j + (i + 1)) - table.get(0).get(j)));
		}
	}
	
	//Storing the interpolating polynomial before simplification as a string
	public static String generatePolynomial(ArrayList<ArrayList<Float>> table){
		String polynomial = "";
		
		for(int i = 1; i < table.size(); ++i){
			
			//Determining sign of coefficient
			if(table.get(i).get(0) < 0 && i != 1)
				polynomial += " - ";
			else if(table.get(i).get(0) > 0 && i != 1){
				polynomial += " + ";
			}
			
			if(table.get(i).get(0) != 0){
				polynomial += Math.abs(table.get(i).get(0));
			
				//Generating appropriate x values
				for(int j = 0; j < i - 1; ++j){
					if(table.get(0).get(j) == 0){
						polynomial += "x";
					}else if(table.get(0).get(j) > 0){
						polynomial += "(x - " + Math.abs(table.get(0).get(j)) + ")";
					}else{
						polynomial += "(x + " + Math.abs(table.get(0).get(j)) + ")";
					}
				}
			}
		}
		
		return polynomial;
	}
	
	public static void display(ArrayList<ArrayList<Float>> table) throws FileNotFoundException{
		File outputFile = new File("output.txt");
		PrintWriter writer = new PrintWriter(outputFile);
		
		//Test display of values to output file
		for(int i = 0; i < table.size(); ++i)
			writer.println(Arrays.toString(table.get(i).toArray()));
				
		//Display of interpolating polynomial to output file
		writer.println("\nInterpolating polynomial is:");
		writer.println(generatePolynomial(table));
				
		//Display of simplified interpolating polynomial to output file
		writer.println("\nSimplified polynomial is:");
		
		writer.close();
	}
}