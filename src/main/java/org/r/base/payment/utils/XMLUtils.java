package org.r.base.payment.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2019/3/27.
 */
public class XMLUtils {

    /**
     * 源字符串
     */
    private String content;

    /**
     * 把源字符串解析出来的对象
     */
    private Document doc;

    /**
     * 编码
     */
    private String charSet = "UTF-8";

    public XMLUtils(String content) {
        this.content = content;
        SAXBuilder sb = new SAXBuilder();
        try {
            this.doc = sb.build(new ByteArrayInputStream(content.getBytes(charSet)));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public Element getElement(Element parent, String name) {
        List elements = this.getElements(parent, name);
        if (elements.size() != 1) return null;
        return (Element) elements.get(0);
    }

    public List getElements(Element parent, String name) {

        return parent.getChildren(name);
    }

    public Element getRootElement() {
        if (this.doc == null) {
            return null;
        }
        return this.doc.getRootElement();
    }

    public String getText(Element startEle, String eleName) {
        List<String> texts = getTexts(startEle, eleName);
        if (texts == null || texts.size() != 1) return "";
        return texts.get(0);
    }


    private List<String> getTexts(Element startEle, String eleName) {
        List children = startEle.getChildren();
        List<String> resList = new ArrayList<>();
        for (Object child : children) {
            Element ele = (Element) child;
            if (eleName.equalsIgnoreCase(ele.getName())) {
                resList.add(ele.getText());
            }
            List subChildren = ele.getChildren();
            if (subChildren.size() == 0) {
                continue;
            }
            resList.addAll(this.getTexts(ele, eleName));
        }
        return resList;
    }


}
