package client;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class PolyModel extends DrawableObject {

	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	public Vector<Polygon> polygons;
	public Vector<String> textureTable;
	public Vector<Texture> textures;
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public PolyModel() {
		polygons = new Vector<Polygon>();
		textureTable = new Vector<String>();
		textures = new Vector<Texture>();
	}
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public boolean loadModel(String filename){
		
		boolean failure = false;
		
		// clear out the vectors
		polygons.clear();
		textureTable.clear();
		
		try {
			
			// open the file
			URL worldURL = getClass().getResource(filename);
			File worldFile = new File(worldURL.toURI());
			BufferedReader in = new BufferedReader(new FileReader(worldFile));
			String line = this.skipCommentsAndBlanks(in);
			
			// read the number of polygons
			int numPolys = Integer.parseInt(line);
			line = this.skipCommentsAndBlanks(in);
			
			// read the texture table
			int numTextures = Integer.parseInt(line);
			
			for (int i = 0; i < numTextures; i++) {
				
				// skip id
				in.readLine();
				line = in.readLine();
				
				// read filename
				textureTable.add(line);
			}
			
			// read the whole thing
			for (int i = 0; i < numPolys; i++) {
				polygons.add(this.readPolygon(in));
			}
			
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			failure = true;
		} catch (IOException e) {
			e.printStackTrace();
			failure = true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			failure = true;
		}
		
		return !failure;
	}
	
	public void loadTextures(GL gl) throws GLException, IOException {
		
		// get all textures
		for (int texID = 0; texID < textureTable.size(); texID++) {
			
			URL textureURL = getClass().getResource(textureTable.get(texID));
			textures.add(TextureIO.newTexture(textureURL, false, ".bmp"));
		}
	}

	@Override
	public void gldraw(GL gl) {

		// loop through all of the triangles
		for (int i = 0; i < polygons.size(); i++) {
			
			Polygon tempoly = polygons.get(i);
			
			// bind the correct texture
			textures.get(tempoly.textureID).bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			
			// start drawing
			if (tempoly.getPolyType().equals("quad")) {
				gl.glBegin(GL.GL_QUADS);
			} else {
				gl.glBegin(GL.GL_TRIANGLES);
			}
						
				// normal vector
				gl.glNormal3d(tempoly.normalVector[0], 
						tempoly.normalVector[1],
						tempoly.normalVector[2]);
				
				// draw all vertices
				for (int vertNo = 0; vertNo < tempoly.vertices.length; vertNo++) {
					
					gl.glTexCoord2f(tempoly.vertices[vertNo].s,
							tempoly.vertices[vertNo].t);
					gl.glVertex3f(tempoly.vertices[vertNo].x,
							tempoly.vertices[vertNo].y,
							tempoly.vertices[vertNo].z);
				}
				
			gl.glEnd();
			
		} // end for loop

	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////

	/**
	 * Skips all blank lines and comments. Comments are denoted by "//".
	 * 
	 * @param in
	 *        The BufferedReader that has been set up to read the file.
	 *        
	 * @return Returns the next line in the file that is not a blank line or comment
	 */
	private String skipCommentsAndBlanks(BufferedReader in) throws IOException {
		
		String line = (in.readLine()).trim();
		
		while (line.equals("") || line.startsWith("//")) {
			line = (in.readLine()).trim();
		}
		
		return line;
	}
	
	/**
	 * Reads/parses a specified number of floats from a string using
	 * spaces as a delimiter.
	 * 
	 * @param numInts
	 *        The number of floats to read from the string.
	 *        
	 * @param line
	 *        The string to read the floats from.
	 *        
	 * @return Returns a float array of size numFloats.
	 */
	private Float[] readFloats(int numFloats, String line) {
		
		// process line
		String[] splitVars = line.split(" ");
		Float[] vars = new Float[numFloats];
		
		// copy the values into a new array of the correct size
		int currentInd = 0;
		for (int i=0; i < numFloats; i++) {
			
			// skip all blank strings
			while (splitVars[currentInd].equals("")) {
				currentInd++;
			}
			vars[i] = Float.parseFloat(splitVars[currentInd]);
			
			currentInd++;
		}
		
		return vars;
	}
	
	private Integer[] readInts(int numInts, String line) {
		
		// process line
		String[] splitVars = line.split(" ");
		Integer[] vars = new Integer[numInts];
		
		// copy the values into a new array of the correct size
		int currentInd = 0;
		for (int i=0; i < numInts; i++) {
			
			// skip all blank strings
			while (splitVars[currentInd].equals("")) {
				currentInd++;
			}
			vars[i] = Integer.parseInt(splitVars[currentInd]);
			
			currentInd++;
		}
		
		return vars;
	}
	
	private Polygon readPolygon(BufferedReader in) throws IOException {
		
		Polygon polygon;
		
		// read # of vertices and texture id
		String line = this.skipCommentsAndBlanks(in);
		Integer[] polyInfo = this.readInts(2, line);
		
		// triangle or quad?
		switch (polyInfo[0]) {
			case 4:
				polygon = new Polygon(4);
				break;
			
			default:
				polygon = new Polygon(3);
				break;
		}
		
		polygon.textureID = polyInfo[1];
		
		// read normal vector
		line = in.readLine();
		polygon.normalVector = this.readFloats(3, line);
		
		// read vertex info
		for (int vertexNo = 0; vertexNo < polyInfo[0]; vertexNo++) {
			
			line = in.readLine();
			Float[] vars = this.readFloats(5, line);
			polygon.vertices[vertexNo] = new Vertex(vars[0],  // x
					 								 vars[1],  // y
					 								 vars[2],  // z
					 								 vars[3],  // u
					 								 vars[4]); // v
		}
		
		return polygon;
	}
	
}
