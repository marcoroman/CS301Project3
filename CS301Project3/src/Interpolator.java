import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * This class generates an interpolating polynomial based on input read from a file
 * The polynomial is generated via Newton's divided difference method and is
 * output to a text file in raw and simplified form.
 * */
public class Interpolator{
	static DecimalFormat decimalFormat = new DecimalFormat("#.####");
	static File outputFile = new File("output.txt");
	static PrintWriter writer;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		writer = new PrintWriter(outputFile);
		ArrayList<ArrayList<Float>> dividedDifferenceTable = new ArrayList<>();
		
		//Generating the divided differences based on input data
		createDividedDifferenceTable(dividedDifferenceTable);
		
		displayTable(dividedDifferenceTable);
		
		//Display of interpolating polynomial to output file
		writer.println("\nInterpolating polynomial is:");
		writer.println(generatePolynomial(dividedDifferenceTable));
						
		//Display of simplified interpolating polynomial to output file
		writer.println("\nSimplified polynomial is:");
		generateSimplifiedPolynomial(dividedDifferenceTable);
				
		writer.close();
	}
	
	//Divided difference table stored as an ArrayList of ArrayLists
	//Generated based on provided input file
	public static void createDividedDifferenceTable(ArrayList<ArrayList<Float>> table) throws FileNotFoundException{
		
		File inputFile = new File("input.txt");
		Scanner reader = new Scanner(inputFile);
		
		ArrayList<Float> data = new ArrayList<>();
		
		int count = 0;
		
		//Reading all values from given input file
		//At most 50 nodes (50 x values + 50 f(x) values)
		while(reader.hasNext() && count++ < 100)
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
				polynomial += decimalFormat.format(Math.abs(table.get(i).get(0)));
			
				//Generating appropriate x values
				for(int j = 0; j < i - 1; ++j){
					if(table.get(0).get(j) == 0){
						polynomial += "x";
					}else if(table.get(0).get(j) > 0){
						polynomial += "(x-" + decimalFormat.format(Math.abs(table.get(0).get(j))) + ")";
					}else{
						polynomial += "(x+" + decimalFormat.format(Math.abs(table.get(0).get(j))) + ")";
					}
				}
			}
		}
		
		return polynomial;
	}
	
	//Generating the simplified polynomial based on string representation
	//of original interpolating polynomial
	public static void generateSimplifiedPolynomial(ArrayList<ArrayList<Float>> table){
		
		//Polynomial lists stores new Polynomial objects created from parsing doubles from difference table
		//Each sublist will then be consolidated into a single polynomial via multiplication
		ArrayList<ArrayList<Polynomial>> polynomialLists = new ArrayList<>();
		
		//Generating polynomial clusters to be multiplied together based on difference table values
		for(int i = 1; i < table.size(); ++i){
			polynomialLists.add(new ArrayList<>());
			
			polynomialLists.get(polynomialLists.size() - 1).add(new Polynomial((double) table.get(i).get(0)));
			
			//Creating polynomials from input values of x
			for(int j = 0; j < i - 1; ++j)
				polynomialLists.get(polynomialLists.size() - 1).add(new Polynomial(1, -1.0 * table.get(0).get(j)));
			
			//Consolidating polynomials by multiplying polynomial clusters
			while(polynomialLists.get(polynomialLists.size() - 1).size() > 1){
				polynomialLists.get(polynomialLists.size() - 1).add(polynomialLists.get(polynomialLists.size() - 1).get(0).multiply(polynomialLists.get(polynomialLists.size() - 1).get(1)));
				polynomialLists.get(polynomialLists.size() - 1).remove(1);
				polynomialLists.get(polynomialLists.size() - 1).remove(0);
			}
		}
		
		//Transferring polynomials from an ArrayList of ArrayLists
		//to an ArrayList of Polynomials for ease of access
		ArrayList<Polynomial> polynomials = new ArrayList<>();
		
		for(int i = 0; i < polynomialLists.size(); ++i){
			polynomials.add(polynomialLists.get(i).get(0));
		}
		
		//Consolidating polynomials by adding polynomial clusters
		while(polynomials.size() > 1){
			polynomials.add(new Polynomial(polynomials.get(0), polynomials.get(1)));
			polynomials.remove(1);
			polynomials.remove(0);
		}
		
		//Final consolidated polynomial output to text file
		writer.println(polynomials.get(0));
	}
}

//Polynomial class used for the construction of simplified polynomial
//Handles multiplication and addition functionality
final class Polynomial{
	private ArrayList<Double> terms;
	private ArrayList<Integer> xPowers;
	static DecimalFormat decimalFormat = new DecimalFormat("#.###");
	
	//Default constructor
	public Polynomial(){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
	}
	
	//Constructor for constants
	public Polynomial(Double d){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.add(d);
		xPowers.add(0);
	}
	
	//Constructor for x
	public Polynomial(int p){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.add(1.0);
		xPowers.add(p);
	}
	
	//Constructor for first order polynomial
	//of form (x+c) or (x-c)
	public Polynomial(int n, Double d){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.add(1.0);
		xPowers.add(1);
		
		terms.add(d);
		xPowers.add(0);
	}
	
	//Constructor for the sum of two polynomials
	public Polynomial(Polynomial p1, Polynomial p2){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.addAll(p1.getTermSet());
		xPowers.addAll(p1.getPowerSet());
		terms.addAll(p2.getTermSet());
		xPowers.addAll(p2.getPowerSet());
		
		this.combineLikeTerms();
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
	
	//Returns a Polynomial product
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
	
	//Returns full formatted polynomial as a string
	public String toString(){
		String polyString = "f(x) = ";
		
		//Constructing polynomial based on coefficients and powers of x
		for(int i = 0; i < terms.size(); ++i){
			if(terms.get(i) != 0){
				
				//Determining appropriate arithmetic operation
				if(terms.get(i) < 0 && i != 0){
					polyString += " - ";
				}else if(terms.get(i) > 0 && i != 0){
					polyString += " + ";
				}
				
				//x-coefficient of 1 not displayed (trivial)
				if(!(Math.abs(terms.get(i)) == 1 && xPowers.get(i) == 1)){
					if(i != 0){
						polyString += decimalFormat.format(Math.abs(terms.get(i)));
					}else if(i == 0){
						polyString += decimalFormat.format(terms.get(i));
					}
				}
				
				//Appending appropriate power of x
				if(xPowers.get(i) > 0){
					if(xPowers.get(i) == 1){
						polyString += "x";
					}else
						polyString += "x^" + xPowers.get(i);
				}
			}
		}
		
		return polyString;
	}
}