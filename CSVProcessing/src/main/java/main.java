import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Date;
import java.time.LocalDate;
//args[0] -> Specifies what operations will be executed.
//args[1] -> Name of the file to write/read(dependent upon args[0]).
//args[2] -> Contains the name of a specified client?
//args[3] -> Contains the ID of a specified client.

public class main {
    private static final Logger LOG = LogManager.getLogger(main.class);//Information and errors will be logged.
    //args[2] and args[3] look for a match between clients
    private static ClientData a = new ClientData();
    public static void main(String[] args) throws IOException, ParseException {
        String password = getPassword("cscPassword.txt");
        ArrayList<String[]> csvContent = formCSVData("clients.csv");//Put CSV data in format of ArrayList.

        if (args[0].equals("query")) {
            a.setFileName(args[1]);
            csvToJSONFile(csvContent);//Converts entire CSV file to JSON format and outputs a file.
            a.setClient(lookUpClient(Integer.parseInt(args[3]), csvContent));//Look for specified client using their client number.
            findCity("csc", "woz.cs.missouriwestern.edu", password, "Chicago", "Illinois");//Find a specified city.
            writeFoundClient(a, args[1]);
            currency(args[4]);
        }else if(args[0].equals("report")){
            printReport(args[1]);//Read specified file and output it in Markdown format.
        }
    }

    public static void writeFoundClient(ClientData client, String fileName)throws IOException{
        FileWriter fileWriter = new FileWriter(fileName);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        fileWriter.write(gson.toJson(client));
        fileWriter.close();
    }
   public static ArrayList<String[]> formCSVData(String csvFile) throws IOException
   {
       ArrayList<String[]> csvContent = new ArrayList<>();
       try {
           CSVReader reader = new CSVReader(new FileReader(csvFile));
           String[] nextLine;
           while ((nextLine = reader.readNext()) != null)//Sets data into nextLine variable
           {
               csvContent.add(nextLine);//Delete whitespace between data
           }
           LOG.info("Total records read: %d\"", csvContent.size()-1);//The row at the 0th index does not count as a record.
           LOG.info(csvContent.get(1));
       } catch (CsvValidationException e) {
           LOG.error("An error has occurred.\n" + e.getMessage());
           System.exit(1);
       }
       return csvContent;
   }

    public static void csvToJSONFile(ArrayList<String[]> csvContent) {
        String[] columnData = csvContent.get(0);//The 0th index specifies the purpose of each column.
        try {
            FileWriter file = new FileWriter("clients.json");
            LOG.info("clients.json was created");
            for (int i = 0; i < csvContent.size(); i++) {
                JSONObject csvToJson = new JSONObject();
                String[] currentData = csvContent.get(i);
                for (int j = 0; j < currentData.length; j++) {
                    csvToJson.put(columnData[j], currentData[j]);//0th index and current index i placed within a single
                                                                 //JSON object j times.
                }
                file.write(csvToJson.toJSONString() + "\n");//Write the newly formed JSON to the report file.
            }
            file.close();
        } catch (IOException e) {
            LOG.error("Error at csvToJSONFile: %s", e.getMessage());
            System.exit(1);
        }

    }
    public static void printReport(String reportName)throws ParseException{
        try {
            Reader reader = Files.newBufferedReader(Paths.get(reportName));
            FileWriter file = new FileWriter("clientreports.md");
            Gson gson = new Gson();
                ClientData data = gson.fromJson(reader, ClientData.class);//Converts data from a JSON file to a JSONObject.
                file.write(
                            "#Client report for " + data.getClient().getFirst_name() + " " + data.getClient().getLast_name()
                        +       "\n ## Client ID " + data.getClient().getNumber() +
                                "\n ##Contact Info" +
                                "\n#### " + data.getClient().getAddress() +
                                "\n#### " + data.getClient().getPhone1() +
                                "\n#### " + data.getClient().getPhone2() +
                                "\n#### " + data.getClient().getEmail() +
                                "\n ##Company" +
                                "\n#### " + data.getClient().getCompany() +
                                "\n#### " + data.getClient().getWeb() +
                                "\n ##Geography" +
                                "\n#### " + data.getClient().getCity() +
                                ", " +        data.getClient().getState() +
                                "\n#### " + data.getClient().getRisk_factor() +
                                    "\n#### " + data.getLatitude() +
                                    "\n#### " + data.getLongitude() +
                                    "\n#### " + data.getMedianAge() +
                                    "\n#### " + data.getTotal_population() +
                                    "\n\n"
                );
            file.close();
        } catch(IOException e){
            System.out.println("IOException has occurred " + e.getMessage());
        }
    }

    public static Client lookUpClient(int givenNum, ArrayList<String[]> csvContent) throws IOException, ParseException {
        Client matchedClient = null;
        for (int i = 1; i < csvContent.size(); i++) {//Start at 1st index, 0th index is not real data.
            if (Integer.parseInt(csvContent.get(i)[0]) == givenNum) {//Check if current client number is the number we are searching for.
                matchedClient = new Client(csvContent.get(i));//Create a client object out of the found client.
                LOG.info(matchedClient.toString());//Record the data of found client.
                URL zipLookUp = new URL("https://api.zippopotam.us/us/" + matchedClient.getPostal_code());
                BufferedReader in = new BufferedReader(new InputStreamReader(zipLookUp.openStream()));
                JSONParser parser = new JSONParser();
                JSONObject zipInfo = (JSONObject) parser.parse(in);//Read in data from URL as a JSONObject.
                String c = zipInfo.get("places").toString();//The key "places" contains the longitude and latitude data we want to retrieve.
                c = c.substring(1, c.length()-1);//Remove the [ ] brackets from the string.
                JSONObject editedData = (JSONObject)parser.parse(c);//Convert string to a JSONObject.
                LOG.info(matchedClient.getPostal_code() + " " + editedData.get("longitude") + " " + editedData.get("latitude"));
                a.setLongitude(Double.parseDouble(editedData.get("longitude").toString()));
                a.setLatitude(Double.parseDouble(editedData.get("latitude").toString()));
//place in JSON
                //intemerdiate object
            }
        }
        return matchedClient;
    }
    public static String getPassword(String fileName) {
        String password = "";
        try {
            Scanner input = new Scanner(new File(fileName));
            password = input.nextLine().trim();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return password;
    }

    public static void findCity(String user, String host, String password, String city, String state){
        String connectionString = String.format("jdbc:mariadb://%s:3306/misc", host);//Establish connection to the database
        try{
            Connection conn = DriverManager.getConnection(connectionString, user, password);
            String cityStatequery = "SELECT * FROM usCityDemographics WHERE city LIKE ? AND state LIKE ?";
            PreparedStatement csStatement = conn.prepareStatement(cityStatequery);
            csStatement.setString(1, city);
            csStatement.setString(2, state);
            ResultSet csResult = csStatement.executeQuery();//Look for data for the specified city and state.
            if(csResult.next()) {//If data is found...
                LOG.info(csResult.getString("medianage") + " " + csResult.getString("total_population"));
                double medianAge = Double.parseDouble(csResult.getString("medianage"));
                a.setMedianAge(medianAge);
                a.setTotal_population(csResult.getInt("total_population"));
            }else {//If data is not found...
                String statequery = "SELECT * FROM usCityDemographics WHERE state LIKE ?";
                PreparedStatement sStatement = conn.prepareStatement(statequery);
                sStatement.setString(1, state);
                ResultSet sResult = sStatement.executeQuery();
                if (sResult.next()) {//Check if the state can be found.
                    System.out.println("Expected city was not found.");//If it can, then the city was not recorded in our DB.
                } else {//If the state cannot be found as well, then the state and the city were not recorded in our DB.
                    LOG.info("Expected city and state were not found.");
                }
            }
        }catch(SQLException e)
        {
            LOG.fatal("A SQL Error has occured.\n" + e.getMessage());
            System.exit(1);
        }
    }

    public static void currency(String currencyType) throws IOException, ParseException {
        URL currencyLookUp = new URL("http://www.floatrates.com/daily/usd.json");
        BufferedReader in = new BufferedReader(new InputStreamReader(currencyLookUp.openStream()));
        JSONParser parser = new JSONParser();
        JSONObject currencyData = (JSONObject)parser.parse(in);
        JSONObject chosenCurrency = (JSONObject)currencyData.get(currencyType);
        Gson gson = new Gson();
        HashMap<String, Map> data = gson.fromJson(chosenCurrency.toJSONString(), HashMap.class);
        String websiteDate = String.valueOf(data.get("date"));
        String tempDate = websiteDate.replaceAll(".*,", "");
        System.out.println(tempDate);
        System.out.printf("Specified currency is: " + "%s" + "\nRate of exchange is: %f %s per USD" ,data.get("name"), data.get("rate"), currencyType);
    }


}
class ClientData{
    String fileName;
    Client client;
    double latitude;
    double longitude;
    double medianAge;
    int total_population;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public void setMedianAge(double medianAge) {
        this.medianAge = medianAge;
    }

    public int getTotal_population() {
        return total_population;
    }

    public void setTotal_population(int total_population) {
        this.total_population = total_population;
    }
}
