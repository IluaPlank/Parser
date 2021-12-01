import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCsv = parseCSV(columnMapping, fileName);
        List<Employee> listXml = parseXML("data.xml");
        String jsonCsv = listToJson(listCsv);
        String jsonXml = listToJson(listXml);
        writeString(jsonCsv, 1);
        writeString(jsonXml, 2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employee = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            employee = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public static String listToJson(List list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list);
    }

    public static void writeString(String data, int number) {
        try (FileWriter file = new
                FileWriter("data" + number + ".json")) {
            file.write(data);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xml);
        Node root = doc.getDocumentElement();
        return read(root);
    }

    private static List<Employee> read(Node node) {
        List<String> textNode = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                String elementText = element.getTextContent();
                String[] text = elementText.split("\n");
                for (int j = 0; j < text.length; j++) {
                    text[j] = text[j].trim();
                    if (!Objects.equals(text[j], "")) {
                        textNode.add(text[j]);
                    }
                }
            }
        }
        List<Employee> employee = new ArrayList<>();
        for (int i = 0; i < textNode.size(); i += 5) {
            employee.add(new Employee(Long.parseLong(textNode.get(i)), textNode.get(i + 1), textNode.get(i + 2),
                    textNode.get(i + 3), Integer.parseInt(textNode.get(i + 4))));
        }
        return employee;
    }
}
