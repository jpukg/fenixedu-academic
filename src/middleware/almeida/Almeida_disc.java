
// Generated by impart OJB Generator
// www.impart.ch matthias.roth@impart.ch

package middleware.almeida;


public class Almeida_disc  
{
  private long anodis;

  private long codcur;

  private String coddis;

  private long codint;

  private long codram;

  private double lab;

  private String nomedis;

  private double pra;

  private long semdis;

  private double teo;

  private double teopra;

  private long tipo;
  
  private double credits;
  
  private long orientation;

  public long getAnodis()
  {
     return this.anodis;
  }
  public void setAnodis(long param)
  {
    this.anodis = param;
  }


  public long getCodcur()
  {
     return this.codcur;
  }
  public void setCodcur(long param)
  {
    this.codcur = param;
  }


  public String getCoddis()
  {
     return this.coddis;
  }
  public void setCoddis(String param)
  {
    this.coddis = param;
  }


  public long getCodint()
  {
     return this.codint;
  }
  public void setCodint(long param)
  {
    this.codint = param;
  }


  public long getCodram()
  {
     return this.codram;
  }
  public void setCodram(long param)
  {
    this.codram = param;
  }


  public double getLab()
  {
     return this.lab;
  }
  public void setLab(double param)
  {
    this.lab = param;
  }


  public String getNomedis()
  {
     return this.nomedis;
  }
  public void setNomedis(String param)
  {
    this.nomedis = param;
  }


  public double getPra()
  {
     return this.pra;
  }
  public void setPra(double param)
  {
    this.pra = param;
  }


  public long getSemdis()
  {
     return this.semdis;
  }
  public void setSemdis(long param)
  {
    this.semdis = param;
  }


  public double getTeo()
  {
     return this.teo;
  }
  public void setTeo(double param)
  {
    this.teo = param;
  }


  public double getTeopra()
  {
     return this.teopra;
  }
  public void setTeopra(double param)
  {
    this.teopra = param;
  }


  public long getTipo()
  {
     return this.tipo;
  }
  public void setTipo(long param)
  {
    this.tipo = param;
  }


  public String toString(){
    return  " [anoDis] " + anodis + " [codCur] " + codcur + " [codDis] " + coddis + " [codInt] " + codint + " [codRam] " + codram + " [lab] " + lab + " [nomeDis] " + nomedis + " [pra] " + pra + " [semDis] " + semdis + " [teo] " + teo + " [teoPra] " + teopra + " [tipo] " + tipo;

  }
/**
 * @return
 */
public double getCredits() {
	return credits;
}

/**
 * @return
 */
public long getOrientation() {
	return orientation;
}

/**
 * @param credits
 */
public void setCredits(double credits) {
	this.credits = credits;
}

/**
 * @param orientation
 */
public void setOrientation(long orientation) {
	this.orientation = orientation;
}

}

