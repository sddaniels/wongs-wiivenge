package client;

public class Polygon {

	public Vertex[] vertices;
	public int textureID;
	public Float[] normalVector;
	private String type;
	
	public Polygon(int numVerts) {
		
		if (numVerts == 4) {
			
			type = "quad";
			vertices = new Vertex[4];
			
		} else {
			
			vertices = new Vertex[3];
			type = "triangle";
		}
		
		normalVector = new Float[3];
	}
	
	public String getPolyType() {
		return type;
	}
}
