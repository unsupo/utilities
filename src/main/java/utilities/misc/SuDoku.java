package utilities.misc;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SuDoku {
	public static void main(String[] args) throws IOException{
		int[][][] a = readFile("D:\\Portable\\Documents\\school\\CS5890\\p096_sudoku.txt");
		int sum = 0;
		for(int[][] val : a){
			print(val);
			int[][] v = solve(val, new int[]{1,2,3,4,5,6,7,8,9});
			int i = Integer.parseInt(v[0][0]+""+v[0][1]+""+v[0][2]);
			sum+=i;
			System.out.println();
			print(val);
			
			System.out.println();
		}
		System.out.println(sum);
	}
	
	private static void print(int[][] v) {
		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v[i].length; j++) {
				System.out.print(v[i][j]);
			}
			System.out.println();
		}
	}

	private static int[][] solve(int[][] grid, int[] allowed) {
		return _solve(0, 0, grid, allowed);
	}

	private static int[][] _solve(int i, int j, int[][] grid, int[] allowed) {
		if(i>=grid.length && j >= grid[0].length)
			return grid;

		if(i<grid.length){
			if(j<grid.length){
				if(grid[i][j]!=0){
					return _solve(++i, j, grid, allowed);
				}
			}else{
				j = 0;
				if(grid[i][j]!=0){
					return _solve(++i, j, grid, allowed);
				}
			}
		}else{
			i = 0; j++;
			if(j<grid.length){
				if(grid[i][j]!=0){
					return _solve(i, j, grid, allowed);
				}
			}else{
				return grid;
			}
		}
		for (int k = 0; k < allowed.length; k++) {
			if(check(grid,i,j,allowed[k])){
				grid[i][j] = allowed[k];
				int[][] t = _solve(i, j, grid, allowed);
				if(t!=null) return grid;
				grid[i][j] = 0;
			}
		}	
		return null;
	}

	private static boolean check(int[][] grid, int i, int j, int check) {
		if(!check1d(grid[i],check))
			return false;
		if(!check1d(column(grid,j),check))
			return false;
		if(!check1d(box(grid,i,j),check))
			return false;
		return true;
	}
	private static int[] box(int[][] grid, int i, int j) {
		int[] value = new int[grid.length];
		int ni = i%3, nj = j%3, m = 0;
		for (int k = i-ni; k < 3+i-ni; k++) 
			for (int k2 = j-nj; k2 < 3+j-nj; k2++) 
				value[m++] = grid[k][k2];
		return value;
	}
	private static int[] column(int[][] grid, int j) {
		int[] value = new int[grid.length];
		for (int i = 0; i < grid.length; i++)
			value[i] = grid[i][j];
		return value;
	}
	private static boolean check1d(int[] grid, int check) {
		for (int i = 0; i < grid.length; i++) 
			if(grid[i] == check)
				return false;
		return true;
	}
	public static int[][][] readFile(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		List<String> v = new ArrayList<>();
		while((line=br.readLine())!=null)
			v.add(line);
		int k = 0, m = 0;
		int[][][] results = new int[50][9][9];
		for (int i = 1; i < v.size(); i++){
			if(v.get(i).contains("G")){
				k++;
				m = 0;
				continue;
			}
			for (int j = 0; j < v.get(i).length(); j++)
				results[k][m][j] = Integer.parseInt(v.get(i).charAt(j)+"");
			m++;
		}
		br.close();
		return results;
	}
}
