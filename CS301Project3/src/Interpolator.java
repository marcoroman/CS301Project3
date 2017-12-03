import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interpolator{
	static DecimalFormat decimalFormat = new DecimalFormat("#.####");
	static File outputFile = new File("output.txt");
	static PrintWriter writer;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		writer = new PrintWriter(outputFile);
		ArrayList<ArrayList<Float>> dividedDifferenceTable = new ArrayList<>();
		
		//Generating the divided differences based on input data
		createDividedDifferenceTable(dividedDifferenceTable);
		String polynomial = generatePolynomial(dividedDifferenceTable);
		
		/*****************************Beginning Polynomial Separation********************************/
		System.out.println(polynomial);
		
		polynomial = polynomial.substring(7, polynomial.length());
		String[] p = polynomial.split("\\s\\+\\s|\\)\\s");
		System.out.println(Arrays.toString(p));
		
		ArrayList<String[]> stringPolynomials = new ArrayList<>();
		ArrayList<ArrayList<Double>> polynomials = new ArrayList<>();
		
		for(int i = 0; i < p.length; ++i){
			stringPolynomials.add(p[i].split("\\)\\(|\\(|\\)"));
			//System.out.println(Arrays.toString((polynomials.get(polynomials.size() - 1))));
			polynomials.add(new ArrayList<>());
			
			for(int j = 0; j < stringPolynomials.get(i).length; ++j){
				if(!stringPolynomials.get(i)[j].contains("x")){
					polynomials.get(polynomials.size() - 1).add(Double.parseDouble(stringPolynomials.get(i)[j].replaceAll("\\s", "")));
				}else if(stringPolynomials.get(i)[j].contains("-")){
					polynomials.get(polynomials.size() - 1).add(1.0);
					polynomials.get(polynomials.size() - 1).add(-1.0 * Double.parseDouble(stringPolynomials.get(i)[j].split("-")[1]));
				}else if(stringPolynomials.get(i)[j].contains("+")){
					polynomials.get(polynomials.size() - 1).add(1.0);
					polynomials.get(polynomials.size() - 1).add(Double.parseDouble(stringPolynomials.get(i)[j].split("+")[1]));
				}else
					polynomials.get(polynomials.size() - 1).add(1.0);
			}
		}
		
		for(int i = 0; i < polynomials.size(); ++i){
			System.out.println(Arrays.toString(polynomials.get(i).toArray()));
		}
		
		//displayTable(dividedDifferenceTable);
		
		//Display of interpolating polynomial to output file
		writer.println("\nInterpolating polynomial is:");
		writer.println(generatePolynomial(dividedDifferenceTable));
						
		//Display of simplified interpolating polynomial to output file
		writer.println("\nSimplified polynomial is:");
				
		writer.close();
	}
	
	//Divided difference table stored as an ArrayList of ArrayLists
	//Generated based on provided input file
	public static void createDividedDifferenceTable(ArrayList<ArrayList<Float>> table) throws FileNotFoundException{
		
		File inputFile = new File("input.txt");
		Scanner reader = new Scanner(inputFile);
		
		ArrayList<Float> data = new ArrayList<>();
		
		//Reading all values from given input file
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
	
	//Prints the formatted divided difference table to an output file
	public static void displayTable(ArrayList<ArrayList<Float>> table) throws FileNotFoundException{
		
		//Formatted display of divided difference table to output file
		writer.printf("%-15s%-15s", "x", "fx");
				
		String header = " , ";
		
		//Printing appropriate column headers for DD table
		for(int i = 2; i < table.size(); ++i){
			writer.printf("%-15s", "f[" + header + "]");
			header += ", ";
		}
				
		writer.println();
				
		//Printing formatted values for DD table
		for(int i = 0; i < table.get(0).size(); ++i){
			for(int j = 0; j < table.size(); ++j){
				if(table.get(j).size() > i){
					writer.printf("%-15s", decimalFormat.format(table.get(j).get(i)));
				}
			}
					
			writer.println();
		}
	}
	
	//Storing the interpolating polynomial (before simplification) as a string
	public static String generatePolynomial(ArrayList<ArrayList<Float>> table){
		String polynomial = "f(x) = ";
		
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
						polynomial += "(x-" + Math.abs(table.get(0).get(j)) + ")";
					}else{
						polynomial += "(x+" + Math.abs(table.get(0).get(j)) + ")";
					}
				}
			}
		}
		
		return polynomial;
	}
}

final class Polynomial{
	private ArrayList<Double> terms;
	private ArrayList<Integer> xPowers;
	
	public Polynomial(){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
	}
	
	public ArrayList<Double> getTermSet(){
		return terms;
	}
	
	public ArrayList<Integer> getPowerSet(){
		return xPowers;
	}
	
	public double getTerm(int index){
		return terms.get(index);
	}
	
	public int getPower(int index){
		return xPowers.get(index);
	}
	
	public void addTerm(double t){
		terms.add(t);
		xPowers.add(0);
	}
	
	public void setPower(int index, int p){
		xPowers.set(index, p);
	}
	
	public Polynomial multiply(Polynomial p2){
		Polynomial product = new Polynomial();
		
		//Generating product polynomial 
		for(int i = 0; i < terms.size(); ++i){
			for(int j = 0; j < p2.getTermSet().size(); ++j){
				product.addTerm(terms.get(i) * p2.getTerm(j));
				product.setPower(j + (p2.getTermSet().size() * i), xPowers.get(i) + p2.getPower(j));
			}
		}
		
		product.combineLikeTerms();
		
		return product;
	}
	
	//Simplifying the polynomial by combining like terms
	public void combineLikeTerms(){
		for(int i = 0; i < terms.size(); ++i){
			for(int j = 0; j < terms.size(); ++j){
				if(i != j){
					if(xPowers.get(i) == xPowers.get(j)){
						terms.set(i, terms.get(i) + terms.get(j));
						terms.remove(j);
						xPowers.remove(j);
					}
				}
			}
		}
	}
	
	public String toString(){
		String str = "f(x) = ";
		
		for(int i = 0; i < terms.size(); ++i){
			if(terms.get(i) != 0){
				
				if(terms.get(i) < 0 && i != 0){
					str += " - ";
				}else if(terms.get(i) > 0 && i != 0){
					str += " + ";
				}
				
				if(Math.abs(terms.get(i)) != 1)
					str += Math.abs(terms.get(i));
				
				if(xPowers.get(i) > 0){
					if(xPowers.get(i) == 1){
						str += "x";
					}else
						str += "x^" + xPowers.get(i);
				}
			}
		}
		
		return str;
	}
}