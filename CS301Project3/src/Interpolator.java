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
		
		//Generating the divided differences based on input data
		ArrayList<ArrayList<Float>> dividedDifferenceTable = createDividedDifferenceTable();
		displayTable(dividedDifferenceTable);
		
		generatePolynomials(dividedDifferenceTable);
				
		writer.close();
	}
	
	//Divided difference table stored as an ArrayList of ArrayLists
	//Generated based on provided input file
	public static ArrayList<ArrayList<Float>> createDividedDifferenceTable() throws FileNotFoundException{
		
		ArrayList<ArrayList<Float>> table = new ArrayList<>();
		
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
		
		ArrayList<Float> prev;
		ArrayList<Float> newData;
		
		//Iteratively filling in the table
		for(int i = 0; i < table.get(0).size() - 1; ++i){
			
			//prev list contains values calculated on previous iteration of algorithm
			prev = table.get(i + 1);
			newData = new ArrayList<>();
			
			//Divided difference calculation
			for(int j = 0; j < table.get(0).size() - (i + 1); ++j)
				newData.add((prev.get(j + 1) - prev.get(j)) / (table.get(0).get(j + (i + 1)) - table.get(0).get(j)));
			
			table.add(newData);
		}
		
		return table;
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
	
	//Original and simplified polynomials generated simultaneously
	//Original polynomial is stored as a string and then printed to output file
	//Simplified polynomial constructed through lists of polynomials multiplied then added together
	public static void generatePolynomials(ArrayList<ArrayList<Float>> table){
		
		String originalPolynomial = "f(x) = ";
		
		/*
		 * multiplicativeCluster list stores new Polynomial objects created from difference table values
		 * 		each cluster is consolidated by multiplication
		 * Each consolidated cluster is added to additiveCluster; this list of polynomials
		 * 		is consolidated by addition
		 */
		ArrayList<Polynomial> multiplicativeCluster;
		ArrayList<Polynomial> additiveCluster = new ArrayList<>();
				
		//Generating polynomial clusters to be multiplied together
		for(int i = 1; i < table.size(); ++i){
			
			/***************************************Original Polynomial***************************************/
			//Determining appropriate arithmetic sign between terms
			if(table.get(i).get(0) < 0 && i != 1)
				originalPolynomial += " - ";
			else if(table.get(i).get(0) > 0 && i != 1){
				originalPolynomial += " + ";
			}
			
			if(table.get(i).get(0) != 0){
				
				//Coefficient of 1 not displayed unless it is a constant
				if(table.get(i).get(0) != 1 || i == 1)
					originalPolynomial += decimalFormat.format(Math.abs(table.get(i).get(0)));
			
				//Generating appropriate x values
				for(int j = 0; j < i - 1; ++j){
					if(table.get(0).get(j) == 0){
						originalPolynomial += "x";
					}else if(table.get(0).get(j) > 0){
						originalPolynomial += "(x-" + decimalFormat.format(Math.abs(table.get(0).get(j))) + ")";
					}else{
						originalPolynomial += "(x+" + decimalFormat.format(Math.abs(table.get(0).get(j))) + ")";
					}
				}
			}
			/***************************************************************************************************/
			
			/***************************************Simplified Polynomial***************************************/
			multiplicativeCluster = new ArrayList<>();
					
			multiplicativeCluster.add(new Polynomial(table.get(i).get(0)));
					
			//Creating polynomials from input values of x
			for(int j = 0; j < i - 1; ++j)
				multiplicativeCluster.add(new Polynomial(1, (float) -1.0 * table.get(0).get(j)));
					
			//Consolidating polynomials by multiplying polynomial clusters
			while(multiplicativeCluster.size() > 1){
				multiplicativeCluster.add(multiplicativeCluster.get(0).multiply(multiplicativeCluster.get(1)));
				multiplicativeCluster.remove(1);
				multiplicativeCluster.remove(0);
			}
					
			additiveCluster.add(multiplicativeCluster.get(0));
			
			/***************************************************************************************************/
		}
				
		//Consolidating polynomials by adding polynomial clusters (simplified form)
		while(additiveCluster.size() > 1){
			additiveCluster.add(new Polynomial(additiveCluster.get(0), additiveCluster.get(1)));
			additiveCluster.remove(1);
			additiveCluster.remove(0);
		}
		
		//Display of original interpolating polynomial to output file
		writer.println("\nInterpolating polynomial is:");
		writer.println(originalPolynomial);
								
		//Display of simplified interpolating polynomial to output file
		writer.println("\nSimplified polynomial is:");
				
		//Final consolidated polynomial output to text file
		writer.println(additiveCluster.get(0));
	}
}

//Polynomial class used for the construction of simplified polynomial
//Handles multiplication and addition functionality
final class Polynomial{
	private ArrayList<Float> terms;
	private ArrayList<Integer> xPowers;
	static DecimalFormat decimalFormat = new DecimalFormat("#.###");
	
	//Default constructor
	public Polynomial(){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
	}
	
	//Constructor for constants
	public Polynomial(Float d){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.add(d);
		xPowers.add(0);
	}
	
	//Constructor for first order polynomial
	//of form (x+c) or (x-c)
	public Polynomial(int n, Float d){
		terms = new ArrayList<>();
		xPowers = new ArrayList<>();
		
		terms.add((float) 1.0);
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
	
	public ArrayList<Float> getTermSet(){
		return terms;
	}
	
	public ArrayList<Integer> getPowerSet(){
		return xPowers;
	}
	
	public float getTerm(int index){
		return terms.get(index);
	}
	
	public int getPower(int index){
		return xPowers.get(index);
	}
	
	public void addTerm(Float t){
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
				if(terms.get(i) < 0 && i != 0 && terms.get(i - 1) != 0){
					polyString += " - ";
				}else if(terms.get(i) > 0 && i != 0 && terms.get(i - 1) != 0){
					polyString += " + ";
				}
				
				//x-coefficient of 1 not displayed (trivial)
				if(!(Math.abs(terms.get(i)) == 1 && xPowers.get(i) > 0)){
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