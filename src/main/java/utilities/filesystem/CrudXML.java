package utilities.filesystem;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class CrudXML {

	public static void main(String[] args) throws JDOMException, IOException,
			NoSuchFieldException {
		// String file = "C://Users//jarndt//Documents//xml//TestXML.xml";
		// CrudXML c = new CrudXML(file);
		// c.newRoot("adifferentroot");
		// c.addTag("example");
		// c.addTag("example", "childofexample");
		// for(int i = 0; i<100; i++){
		// c.addTag("example", "a"+i);
		// }
		// for(int i = 0; i<100; i++){
		// c.addTag("a"+i, "b"+i);
		// }
		// for(int i = 0; i<100; i++){
		// c.addTag("b"+i, "c"+i);
		// }
		// c.addAttribute(null, "testattribute", "somevalue");
		// c.addAttribute("b2", "testattribute", "somevalue");
		// c.addAttribute("c5", "testattribute", "somevalue");
		// c.addValue("a0","value");
		// c.addValue(null, "some content");
		// c.addValue("c97", "i'm awesome");
		// for(int i = 0; i<20; i++){
		// c.removeTag("a"+i);
		// }

		// c.update(c.getAttributeName(), "b20", "diffatt:diff");
		// c.removeAttribute("b20", "diffatt");

		// String file = "C:\\cygwin64\\home\\jarndt\\bootstrap.xml";
		// CrudXML c = new CrudXML(file);
		// c.insert(c.insert(c.getListofElements().get(0), "HELLO"),"HI");
		// c.delete("HELLO");

		// for(Element e : c.getListofElements())
		// c.insert(e, "ASTUPIDaddedintag");

		// for(int i = 0; i<100; i++){
		// c.addTag("poop");
		// }
		// c.deleteAll("poop");

		CrudXML c = new CrudXML(
				"C:\\Users\\jarndt\\Documents\\jmetertesting\\resources\\HTTP Request.jmx");
		List<Element> aa = c.findAllContains("HTTPSampler.domain");
		c.updateContent(aa.get(0), "another test");
	}

	private List<Element> a;
	private Document doc;
	private String file;
	private String CONTENT = "Content----", ATTRIBUTE = "Attributes----";

	public List<Element> getListofElements() {
		return a;
	}

	public Document getDocument() {
		return doc;
	}

	public String getFile() {
		return file;
	}

	public String getContentName() {
		return CONTENT;
	}

	public String getAttributeName() {
		return ATTRIBUTE;
	}

	public void setListofElements(List<Element> elements) {
		this.a = elements;
	}

	public void setDocument(Document document) {
		this.doc = document;
	}

	public void setFile(String file) throws JDOMException, IOException {
		this.file = file;
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(file);
		a = doc.getRootElement().getChildren();
	}

	public CrudXML(String file) throws JDOMException, IOException {
		this.file = file;
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(file);
		} catch (IOException e) {
			System.err.println("No file... creating one: " + file);
			byte dataToWrite[] = "<root />".getBytes();
			FileOutputStream out = new FileOutputStream(file);
			out.write(dataToWrite);
			out.close();

			doc = builder.build(file);
		} catch (JDOMParseException pe) {
			System.err
					.println("Bad file format... fixing it by putting it in comments: "
							+ file + "\n\n" + pe);
			BufferedReader br = null;

			String everything = "<root><!--\n";
			String sCurrentLine;

			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				everything += sCurrentLine.replace("--", "==") + "\n";
			}
			everything += "--></root>";
			if (br != null)
				br.close();

			byte dataToWrite[] = everything.getBytes();
			FileOutputStream out = new FileOutputStream(file);
			out.write(dataToWrite);
			out.close();

			doc = builder.build(file);
		}
		a = doc.getRootElement().getChildren();
	}

	public void update(String oldTag, String newTag) throws IOException,
			NoSuchFieldException {
		if (doc.getRootElement().toString().equals(oldTag)) {
			newRoot(newTag);
		} else {
			Element e = recurseFind(a, oldTag);
			if (e == null) {
				throw new NoSuchFieldException("No tag named: " + oldTag);
			}
			e.setName(newTag);
			writeXML(doc);
		}

	}

	public Element updateContent(Element tag, String newContent) throws IOException,
			NoSuchFieldException {
		Element e = null;
		if (tag.equals(doc.getRootElement())) {
			doc.setRootElement(tag);
		} else {
			e = recurseFind(a, tag);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ tag.getName());
			}
			e = e.setText(newContent);
		}
		writeXML(doc);
		return e;
	}

	public void update(String type, String oldTag, String newValue)
			throws IOException, NoSuchFieldException {
		Element e = null;
		if (doc.getRootElement().getName().equals(oldTag)) {
			e = doc.getRootElement();
		} else {
			e = recurseFind(a, oldTag);
		}
		if (e == null) {
			throw new NoSuchFieldException("No tag named: " + oldTag);
		}
		if (type.equals(ATTRIBUTE)) {
			String attName = newValue.split(":")[0], attValue = newValue
					.split(":")[1];
			if (e.hasAttributes()) {
				for (Attribute a : e.getAttributes()) {
					if (a.getName().equals(attName)
							&& !a.getValue().equals(attValue)) {
						a.setValue(attValue);
						break;
					}
				}
			}
			e.setAttribute(new Attribute(attName, attValue));

		} else if (type.equals(CONTENT)) {
			e.setText(newValue);

		} else {
			update(oldTag, newValue);
		}
		writeXML(doc);
	}

	private Element recurseFind(List<Element> le, String tag) {
		for (Element e : le) {
			if (e.getName().equals(tag)) {
				return e;
			} else {
				Element el = recurseFind(e.getChildren(), tag);
				if (el != null && el.getName().equals(tag)) {
					return el;
				}
			}
		}
		return null;
	}

	private Element recurseFind(List<Element> le, Element element) {
		for (Element e : le) {
			if (e.equals(element)) {
				return e;
			} else {
				Element el = recurseFind(e.getChildren(), element);
				if (el != null && el.equals(element)) {
					return el;
				}
			}
		}
		return null;
	}

	public Element insert(Element parent, Element tobeinserted)
			throws NoSuchFieldException, IOException {
		if (parent.equals(doc.getRootElement())) {
			doc.getRootElement().addContent(tobeinserted);
		} else {
			Element e = recurseFind(a, parent);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ parent.getName());
			}
			e.addContent(tobeinserted);
		}
		writeXML(doc);
		return tobeinserted;
	}

	public Element insert(Element parent, String tobeinserted)
			throws NoSuchFieldException, IOException {
		Element re = new Element(tobeinserted);
		if (parent.equals(doc.getRootElement())) {
			doc.getRootElement().addContent(re);
		} else {
			Element e = recurseFind(a, parent);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ parent.getName());
			}
			e.addContent(re);
		}
		writeXML(doc);
		return re;
	}

	public Element update(Element oldElement, Element newElement)
			throws NoSuchFieldException, IOException {
		if (oldElement.equals(doc.getRootElement())) {
			doc.setRootElement(newElement);
		} else {
			Element e = recurseFind(a, oldElement);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement.getName());
			}
			e = newElement;
		}
		writeXML(doc);
		return newElement;
	}

	public Element update(Element oldElement, String newElement)
			throws NoSuchFieldException, IOException {
		Element re = new Element(newElement);
		if (oldElement.equals(doc.getRootElement())) {
			doc.setRootElement(re);
		} else {
			Element e = recurseFind(a, oldElement);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement.getName());
			}
			e = re;
		}
		writeXML(doc);
		return re;
	}

	public Element delete(Element oldElement) throws NoSuchFieldException,
			IOException {
		Element re;
		if (oldElement.equals(doc.getRootElement())) {
			doc.removeContent();
			re = null;
		} else {
			Element e = recurseFind(a, oldElement);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement.getName());
			}
			e.getParent().removeContent(e);
			re = (Element) e.getParent();
		}
		writeXML(doc);
		return re;

	}

	public Element delete(String oldElement) throws NoSuchFieldException,
			IOException {
		Element re;
		if (oldElement.equals(doc.getRootElement().getName())) {
			doc.removeContent();
			re = null;
		} else {
			Element e = recurseFind(a, oldElement);
			if (e == null) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement);
			}
			e.getParent().removeContent(e);
			re = (Element) e.getParent();
		}
		writeXML(doc);
		return re;
	}

	public void deleteAll(Element oldElement) throws NoSuchFieldException,
			IOException {
		if (oldElement.equals(doc.getRootElement())) {
			doc.removeContent();
		} else {
			List<Element> e = recurseFindAll(a, oldElement);
			if (e.size() == 0) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement.getName());
			}
			for (Element el : e) {
				el.getParent().removeContent(el);
			}
		}
		writeXML(doc);

	}

	public void deleteAll(String oldElement) throws NoSuchFieldException,
			IOException {
		if (oldElement.equals(doc.getRootElement().getName())) {
			doc.removeContent();
		} else {
			List<Element> e = recurseFindAll(a, oldElement);
			if (e.size() == 0) {
				throw new NoSuchFieldException("No element named: "
						+ oldElement);
			}
			for (Element el : e) {
				el.getParent().removeContent(el);
			}
		}
		writeXML(doc);

	}

	public List<Element> findAll(String toBeFound) {
		return recurseFindAllAny(doc.getRootElement().getChildren(), toBeFound);
	}

	public List<Element> findAllContains(String toBeFound) {
		return recurseFindAllAnyContains(doc.getRootElement().getChildren(),
				toBeFound);
	}

	private List<Element> recurseFindAll(List<Element> all, String tobefound) {
		List<Element> ele = new ArrayList<Element>();
		recurseFindAll(ele, all, tobefound);
		return ele;

	}

	private List<Element> recurseFindAllAny(List<Element> all, String tobefound) {
		List<Element> ele = new ArrayList<Element>();
		recurseFindAllAny(ele, all, tobefound);
		return ele;

	}

	private void recurseFindAllAny(List<Element> ele, List<Element> all,
			String tobefound) {
		for (Element e : all) {
			if (e.getName().equals(tobefound)
					|| matchAttributes(e.getAttributes(), tobefound)) {
				ele.add(e);
			}
			recurseFindAllAny(ele, e.getChildren(), tobefound);
		}

	}

	private List<Element> recurseFindAllAnyContains(List<Element> all,
			String tobefound) {
		List<Element> ele = new ArrayList<Element>();
		recurseFindAllAnyContains(ele, all, tobefound);
		return ele;

	}

	private void recurseFindAllAnyContains(List<Element> ele,
			List<Element> all, String tobefound) {
		for (Element e : all) {
			if (e.getName().contains(tobefound)
					|| matchAttributesContains(e.getAttributes(), tobefound)) {
				ele.add(e);
			}
			recurseFindAllAnyContains(ele, e.getChildren(), tobefound);
		}

	}

	private boolean matchAttributes(List<Attribute> attributes, String toBeFound) {
		for (Attribute att : attributes)
			if (att.getName().equals(toBeFound)
					|| att.getValue().equals(toBeFound))
				return true;
		return false;
	}

	private boolean matchAttributesContains(List<Attribute> attributes,
			String toBeFound) {
		for (Attribute att : attributes)
			if (att.getName().contains(toBeFound)
					|| att.getValue().contains(toBeFound))
				return true;
		return false;
	}

	private void recurseFindAll(List<Element> ele, List<Element> all,
			String tobefound) {
		for (Element e : all) {
			if (e.getName().equals(tobefound)) {
				ele.add(e);
			}
			recurseFindAll(ele, e.getChildren(), tobefound);
		}

	}

	public List<Element> recurseFindAll(List<Element> all, Element tobefound) {
		List<Element> ele = new ArrayList<Element>();
		recurseFindAll(ele, all, tobefound);
		return ele;

	}

	private void recurseFindAll(List<Element> ele, List<Element> all,
			Element tobefound) {
		for (Element e : all) {
			if (e.equals(tobefound)) {
				ele.add(e);
			}
			recurseFindAll(ele, e.getChildren(), tobefound);
		}
	}

	public void writeXML(Document newDoc) throws IOException {
		XMLOutputter xo = new XMLOutputter();
		// xo.output(newDoc, System.out);
		xo.setFormat(Format.getPrettyFormat());
		xo.output(newDoc, new FileWriter(file
		// file.replace(file.split("//")[file.split("//").length-1],
		// "anotherfile.xml")
				));

	}

	public void newRoot(String root) throws IOException {
		Element newRoot = new Element(root);
		Document newDoc = new Document(newRoot);

		for (Element e : a) {
			Element elemCopy = (Element) e.clone();
			elemCopy.detach();
			newDoc.getRootElement().addContent(elemCopy);
		}

		for (Attribute at : doc.getRootElement().getAttributes()) {
			newRoot.setAttribute(at);
		}
		List<Comment> content = doc.getRootElement().getContent(
				Filters.comment());
		for (Comment c : content) {
			Comment elemCopy = c.clone();
			elemCopy.detach();
			newRoot.addContent(elemCopy);
		}
		this.doc = newDoc;
		this.a = newDoc.getRootElement().getChildren();
		writeXML(newDoc);
	}

	public void addValue(String parent, String value) throws IOException {
		if (parent == null) {
			doc.getRootElement().addContent(value);
			writeXML(doc);
		} else {
			for (Element e : a) {
				if (e.getName().equals(parent)) {
					e.addContent(value);
					writeXML(doc);
					return;
				}
			}
			recurse(a, parent, value, true);
		}

	}

	public void addAttribute(String parent, String attribute, String value)
			throws IOException {
		if (parent == null || parent.equals(doc.getRootElement().getName())) {
			doc.getRootElement().setAttribute(attribute, value);
			writeXML(doc);
		} else {
			for (Element e : a) {
				if (e.getName().equals(parent)) {
					e.setAttribute(attribute, value);
					writeXML(doc);
					return;
				}
			}
			recurse(a, parent, attribute, value);
		}
	}

	public void removeAttribute(String parent, String attribute)
			throws IOException, NoSuchFieldException {
		if (parent == null || parent.equals(doc.getRootElement().getName())) {
			doc.getRootElement().removeAttribute(attribute);
		} else {
			Element e = recurseFind(a, parent);
			try {
				e.removeAttribute(attribute);
			} catch (NullPointerException npe) {
				throw new NoSuchFieldException("No tag named: " + parent);
			}
		}
		writeXML(doc);
	}

	public void addTag(String parent, String tag) throws IOException {
		if (parent == null || doc.getRootElement().getName().equals(parent)) {
			doc.getRootElement().addContent(new Element(tag));
			writeXML(doc);
		} else {
			for (Element e : a) {
				if (e.getName().equals(parent)) {
					e.addContent(new Element(tag));
					writeXML(doc);
					return;
				}
			}
			recurse(a, parent, tag);
		}

	}

	public Element addTag(String tag) throws IOException {
		Element newElement = new Element(tag);
		doc.getRootElement().addContent(newElement);
		writeXML(doc);
		return newElement;
	}

	public void addTag(String tag, Map<String, Map<String, String>> extras) {
		// map extras has example {attribute:{1,2,3},element:{5}}

	}

	public void addTag(String tag, List<Element> extras) {
		// extras will have a same format as .getChildren()

	}

	public void addTag(String tag, Element extras) {
		// extras will have a same format as .getChildren()

	}

	public void removeTag(String tag) throws IOException {
		for (Element e : a) {
			if (e.getName().equals(tag)) {
				a.remove(e);
				writeXML(doc);
				return;
			}
		}
		recurse(a, tag, tag, "#()remove");
	}

	private void recurse(List<Element> le, String parent, String tag)
			throws IOException {
		for (Element e : le) {
			if (e.getName().equals(parent)) {
				e.addContent(new Element(tag));
				writeXML(doc);
				return;
			}
		}
		for (Element e : le) {
			recurse(e.getChildren(), parent, tag);
		}
	}

	private void recurse(List<Element> le, String parent, String value,
			Boolean b) throws IOException {
		for (Element e : le) {
			if (e.getName().equals(parent)) {
				e.addContent(value);
				writeXML(doc);
				return;
			}
		}
		for (Element e : le) {
			recurse(e.getChildren(), parent, value, b);
		}
	}

	private void recurse(List<Element> le, String parent, String attribute,
			String value) throws IOException {
		for (Element e : le) {
			if (e.getName().equals(parent)) {
				if (value.equals("#()remove")) {
					System.out.println(e.getName());
					le.remove(e);
				} else {
					e.setAttribute(attribute, value);
				}
				writeXML(doc);
				return;
			}
		}
		for (Element e : le) {
			recurse(e.getChildren(), parent, attribute, value);
		}
	}

}