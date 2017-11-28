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
		//createDividedDifferenceTable(dividedDifferenceTable);

		//display(dividedDifferenceTable);
		
		Polynomial p1 = new Polynomial();
		Polynomial p2 = new Polynomial();
		
		p1.addTerm(1);
		p1.setPower(0, 1);
		p1.addTerm(1);
		p1.setPower(1, 0);
		
		p2.addTerm(1);
		p2.setPower(0, 2);
		p2.addTerm(1);
		p2.setPower(1, 1);
		p2.addTerm(2);
		p2.setPower(2, 0);
		
		Polynomial product = p1.multiply(p2);
		System.out.println(product);
		
		Polynomial p3 = new Polynomial();
		p3.addTerm(1);
		p3.setPower(0, 1);
		p3.addTerm(-2);
		p3.setPower(1, 0);
		
		Polynomial product2 = product.multiply(p3);
		System.out.println(product2);
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