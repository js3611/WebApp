package dcwbeans;

public class bean1
{
    private String name = "wibble";
    public void setName( String n ) { name = n; }
    public String getName() { return name; }
    public String stars( int n )
    {
	String s = "";
	while (n-- > 0)
	    {
		s += "*";
	    }
	return s;
    }
}
