package client;

public class Matrix {
	
	/////////////////////////////////
	// public class variables
	/////////////////////////////////
	
	public float[] m = new float[16];
	
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public Matrix() {
		for (int i=0; i<16; i++) {
			m[i] = 0.0f;
		}
	}
	
	public Matrix(float a0,  float a1,  float a2,  float a3, 
	         	  float a4,  float a5,  float a6,  float a7, 
	         	  float a8,  float a9,  float a10, float a11,
	         	  float a12 ,float a13, float a14, float a15) {
		
	    m[0] = a0;  m[4] = a4; m[8]  = a8;  m[12] = a12;
	    m[1] = a1;  m[5] = a5; m[9]  = a9;  m[13] = a13; 
	    m[2] = a2;  m[6] = a6; m[10] = a10; m[14] = a14;
	    m[3] = a3;  m[7] = a7; m[11] = a11; m[15] = a15;
	}
	
	public Matrix(Matrix copyMe) {
		
		for (int i=0; i<16; i++) {
			
			m[i] = copyMe.m[i];
		}
	}
	
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public void identity() {
		
		for (int i=0; i<16; i++) {
			m[i] = 0.0f;
		}
		m[0] = 1.0f;
		m[5] = 1.0f;
		m[10] = 1.0f;
		m[15] = 1.0f;
	}
	
}
