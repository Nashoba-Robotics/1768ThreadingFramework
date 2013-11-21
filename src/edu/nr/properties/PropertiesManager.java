package edu.nr.properties;

import edu.nr.Components.NButton;
import edu.nr.MovableComponent;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author co1in
 *         Date: 11/14/13
 *         Time: 10:34 PM
 */

import java.awt.Point;

public class PropertiesManager
{
    public static ArrayList<MovableComponent> loadElementsFromFile(String path)
    {
        try
        {
            File xmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            System.out.println("Reading from: " + path);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList widgetList = root.getChildNodes();
            for(int i = 0; i < widgetList.getLength(); i++)
            {
                ArrayList<Property> properties = new ArrayList<Property>();
                Node node = widgetList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element)node;
                    String widgetClasstName = element.getTagName();

                    properties.add(new Property(Property.Type.NAME, element.getAttribute("name")));

                    //My apologies for the intensively large amount of method calls that happen on each line in the following block of code :(
                    //I will try to document these lines more thoroughly to make up for it :D

                    //This line gets the location element from our widget, gets the x and y values, and plugs them into a new Point object that we will add to our properties list
                    Point locationPoint = new Point(Integer.parseInt(((Element)element.getElementsByTagName("location").item(0)).getAttribute("x")), Integer.parseInt(((Element)element.getElementsByTagName("location").item(0)).getAttribute("y")));
                    properties.add(new Property(Property.Type.LOCATION, new Point()));
                }
            }
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return null;//FIXME CHANGE THIS TO PROPERTIES
    }

    public static boolean writeAllPropertiesToFile(String path, ArrayList<MovableComponent> components)
    {
        //TODO Add saving of VALUE and FONT_SIZE
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("dashboard");
            doc.appendChild(rootElement);

            for(MovableComponent movableComponent : components)
            {
                Element widget = doc.createElement(movableComponent.getWidgetName());
                rootElement.appendChild(widget);
                ArrayList<Property> properties = movableComponent.getProperties();

                //Write Name
                Property p = Property.getPropertyFromType(Property.Type.NAME, properties);
                assert p != null;
                Attr attr = doc.createAttribute("name");
                attr.setValue(String.valueOf(p.getData()));
                widget.setAttributeNode(attr);

                //Add the location
                p = Property.getPropertyFromType(Property.Type.LOCATION, properties);
                Element location = doc.createElement("location");
                Attr locX = doc.createAttribute("x");
                locX.setValue(String.valueOf(((Point) p.getData()).x));
                Attr locY = doc.createAttribute("y");
                locY.setValue(String.valueOf(((Point) p.getData()).y));
                location.setAttributeNode(locX);
                location.setAttributeNode(locY);
                widget.appendChild(location);

                //Add the size
                p = Property.getPropertyFromType(Property.Type.SIZE, properties);
                Element size = doc.createElement("size");
                Attr width = doc.createAttribute("width");
                width.setValue(String.valueOf(((Dimension)p.getData()).width));
                Attr height = doc.createAttribute("height");
                height.setValue(String.valueOf(((Dimension)p.getData()).height));
                size.setAttributeNode(width);
                size.setAttributeNode(height);
                widget.appendChild(size);

                //Add a background Color
                p = Property.getPropertyFromType(Property.Type.BACKGROUND, properties);
                Element background = doc.createElement("background");
                Color color = (Color)p.getData();
                background.setAttribute("red", String.valueOf(color.getRed()));
                background.setAttribute("green", String.valueOf(color.getGreen()));
                background.setAttribute("blue", String.valueOf(color.getBlue()));
                widget.appendChild(background);

                //Add the foreground Color
                p = Property.getPropertyFromType(Property.Type.FOREGROUND, properties);
                Element foreground = doc.createElement("foreground");
                color = (Color)p.getData();
                foreground.setAttribute("red", String.valueOf(color.getRed()));
                foreground.setAttribute("green", String.valueOf(color.getGreen()));
                foreground.setAttribute("blue", String.valueOf(color.getBlue()));
                widget.appendChild(foreground);

                //Add the type of widget
                p = Property.getPropertyFromType(Property.Type.WIDGET_TYPE, properties);
                Element type = doc.createElement("type");
                type.setAttribute("value", String.valueOf(p.getData()));
                widget.appendChild(type);

                //Add the value
                p = Property.getPropertyFromType(Property.Type.VALUE, properties);
                Element value = doc.createElement("value");
                value.setAttribute("val", String.valueOf(p.getData()));
                widget.appendChild(value);

                //Add the Font size
                //Add the type of widget
                p = Property.getPropertyFromType(Property.Type.FONT_SIZE, properties);
                Element fontSize = doc.createElement("fontSize");
                fontSize.setAttribute("size", String.valueOf(p.getData()));
                widget.appendChild(fontSize);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");


            return true;
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadPropertiesIntoArray(ArrayList<Property> defaultProperties, ArrayList<Property> loadingProperties)
    {
        if(loadingProperties != null)
        {
            for(Property p : loadingProperties)
            {
                for(int i = 0; i < defaultProperties.size(); i++)
                {
                    if(p.getType().equals(defaultProperties.get(i).getType()))
                    {
                        defaultProperties.set(i, p);
                    }
                }
            }
        }
    }
}
