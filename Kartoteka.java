import java.util.StringTokenizer;

public class Kartoteka {

    private final String[] paramNames = {
            "Name of the book",
            "Genre",
            "Language",
            "Date",
            "Date of the first publishing"
    };

    private String data;
    private String[] params = new String[paramNames.length];

    public Kartoteka(String description) {
        StringTokenizer st = new StringTokenizer(description, ";");
        if (st.countTokens() != params.length + 1) {
            throw new IllegalArgumentException(description);
        }

        int i = 0;
        if (st.hasMoreTokens()) {
        	data = st.nextToken();
        }

        while (st.hasMoreTokens() && i < params.length) {
            params[i] = st.nextToken();
            i++;
        }
    }

    public String[] getParameters() {
        return params;
    }

    public String[] getParameterNames() {
        return paramNames;
    }

    public String getData() {
        return data;
    }

    public boolean changeBook(String str) {
        int r = str.indexOf(';');
        if (r <= 0) {
            return false;
        }
        String param = str.substring(0, r);
        String value = str.substring(r + 1);
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equalsIgnoreCase(param)) {
                params[i] = value;
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return  params[0];
    }

    @Override
    public boolean equals(Object o) {
    	Kartoteka obj = (Kartoteka) o;
        if (!data.equals(obj.data)) {
            return false;
        }
        if (!params[0].equals(obj.params[0])) {
            return false;
        }
        return true;
    }
}
