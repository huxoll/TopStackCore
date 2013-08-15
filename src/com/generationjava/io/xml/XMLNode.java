/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name of Genjava-Core nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.generationjava.io.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * An empty enumeration. Nicer to return than just plain null.
 */
class NullEnumeration implements Enumeration<Object> {
	public boolean hasMoreElements() {
		return false;
	}

	public Object nextElement() {
		return null;
	}
}

/**
 * A single enumeration. Saves time on making a Vector.
 */
@SuppressWarnings("rawtypes")
class SingleEnumeration implements Enumeration {

	private Object obj;

	public SingleEnumeration(final Object obj) {
		this.obj = obj;
	}

	public boolean hasMoreElements() {
		return obj != null;
	}

	public Object nextElement() {
		final Object tmp = obj;
		obj = null;
		return tmp;
	}
}

/**
 * An xml tag. It can be a processing instructon, an empty tag or a normal tag.
 * Currently, if the tag is inside a namespace then that is a part of the name.
 * That is, all names of tags are fully qualified by the namespace.
 */
public class XMLNode {

	private static final Enumeration<Object> EMPTY = new NullEnumeration();

	// from Commons.Lang.StringUtils
	private static String replace(final String text, final String repl,
			final String with) {
		int max = -1;
		if (text == null || repl == null || with == null || repl.length() == 0
				|| max == 0)
			return text;

		final StringBuffer buf = new StringBuffer(text.length());
		int start = 0, end = 0;
		while ((end = text.indexOf(repl, start)) != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0) {
				break;
			}
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	private static String unescapeXml(String str) {
		str = replace(str, "&amp;", "&");
		str = replace(str, "&lt;", "<");
		str = replace(str, "&gt;", ">");
		str = replace(str, "&quot;", "\"");
		str = replace(str, "&apos;", "'");
		return str;
	}

	private Hashtable<Object,Object> myAttrs;
	private Hashtable<Object,Object> myNodes; // allows quick lookup
	private Vector<Object> myNodeList; // maintains order of myNodes
	private String name;
	private String value;
	private boolean pi;

	private boolean comment;

	private boolean doctype;

	/**
	 * Empty Constructor.
	 */
	public XMLNode() {
		this("");
	}

	/**
	 * Create a new node with this name.
	 */
	public XMLNode(final String name) {
		this.name = name;
	}

	/**
	 * Add an attribute with specified name and value.
	 */
	public void addAttr(final String name, String value) {
		if (myAttrs == null) {
			myAttrs = new Hashtable<Object,Object>();
		}
		value = unescapeXml(value);
		myAttrs.put(name, value);
	}

	/**
	 * Add a child node to this node.
	 */
	public void addNode(final XMLNode node) {
		if (myNodes == null) {
			myNodes = new Hashtable<Object,Object>();
			myNodeList = new Vector<Object>();
		}
		myNodeList.add(node);
		final Object obj = myNodes.get(node.getName());
		if (obj == null) {
			myNodes.put(node.getName(), node);
		} else if (obj instanceof XMLNode) {
			final Vector<Object> vec = new Vector<Object>();
			vec.addElement(obj);
			vec.addElement(node);
			myNodes.put(node.getName(), vec);
		} else if (obj instanceof Vector) {
			@SuppressWarnings("unchecked")
            final Vector<Object> vec = (Vector<Object>) obj;
			vec.addElement(node);
		}
	}

	/**
	 * Get the String version of the body of this tag.
	 */
	public String bodyToString() {
		final StringBuffer tmp = new StringBuffer();
		final Enumeration<Object> enm = enumerateNode();
		while (enm.hasMoreElements()) {
			final Object obj = enm.nextElement();
			if (obj instanceof XMLNode) {
				final XMLNode node = (XMLNode) obj;
				tmp.append(node);
			} else if (obj instanceof Vector) {
				final Vector<?> nodelist = (Vector<?>) obj;
				final Enumeration<?> nodeEnum = nodelist.elements();
				while (nodeEnum.hasMoreElements()) {
					final XMLNode node = (XMLNode) nodeEnum.nextElement();
					tmp.append(node);
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * Enumerate over all the attributes of this node. In the order they were
	 * added.
	 */
	public Enumeration<Object> enumerateAttr() {
		if (myAttrs == null)
			return EMPTY;
		else
			return myAttrs.keys();
	}

	/**
	 * Enumerate over all of this node's children nodes.
	 */
	public Enumeration<Object> enumerateNode() {
		if (myNodes == null)
			return EMPTY;
		else
			// return this.myNodes.elements();
			return myNodeList.elements();
	}

	// Enumerates a child node. Possibly needs renaming.
	// That is, it enumerates a child nodes value.
	/**
     *
     */
	public Enumeration<?> enumerateNode(final String name) {
		if (myNodes == null)
			return EMPTY;
		final Object obj = myNodes.get(name);
		if (obj == null)
			return EMPTY;
		else if (obj instanceof Vector)
			return ((Vector<?>) obj).elements();
		else
			return new SingleEnumeration(obj);
	}

	/**
	 * Get the attribute with the specified name.
	 */
	public String getAttr(final String name) {
		if (myAttrs == null)
			return null;
		return (String) myAttrs.get(name);
	}

	/**
	 * Get the name of this node. Includes the namespace.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the namespace of this node.
	 */
	public String getNamespace() {
		if (name.indexOf(":") != -1)
			return name.substring(0, name.indexOf(":"));
		else
			return "";
	}

	/**
	 * Get the node with the specified name.
	 */
	public XMLNode getNode(final String name) {
		if (myNodes == null)
			return null;
		final Object obj = myNodes.get(name);
		if (obj instanceof XMLNode)
			return (XMLNode) obj;
		return null;
	}

	/**
	 * Get the tag name of this node. Doesn't include namespace.
	 */
	public String getTagName() {
		if (name.indexOf(":") != -1)
			return name.substring(name.indexOf(":") + 1);
		else
			return name;
	}

	/**
	 * Get the appended toString's of the children of this node. For a text
	 * node, it will print out the plaintext.
	 */
	public String getValue() {
		if (isComment())
			return "<!-- " + value + " -->";
		if (isDocType())
			return "<!DOCTYPE " + value + ">";
		if (value != null)
			return value;
		if (isInvisible())
			return "";
		// QUERY: shouldn't call toString. Needs to improve
		if (myNodeList != null) {
			final StringBuffer buffer = new StringBuffer();
			final Enumeration<?> enm = enumerateNode();
			while (enm.hasMoreElements()) {
				buffer.append(enm.nextElement().toString());
			}
			return buffer.toString();
		}
		return null;
	}

	/**
	 * Is it a comment
	 */
	public boolean isComment() {
		return comment;
	}

	/**
	 * Is it a doctype
	 */
	public boolean isDocType() {
		return doctype;
	}

	// IMPL: Assumes that you're unable to remove nodes from
	// a parent node. removeNode and removeAttr is likely to
	// become a needed functionality.
	/**
	 * Is this node empty.
	 */
	public boolean isEmpty() {
		return myNodes == null;
	}

	/**
	 * Is it invisible
	 */
	public boolean isInvisible() {
		return name == null;
	}

	/**
	 * Is it a processing instruction
	 */
	public boolean isPI() {
		return pi;
	}

	/**
	 * Is this a normal tag? That is, not plaintext, not comment and not a pi.
	 */
	public boolean isTag() {
		return !(pi || name == null || value != null);
	}

	/**
	 * Is this a text node.
	 */
	public boolean isTextNode() {
		return value != null && !comment && !doctype && !pi;
	}

	/**
	 * Set whether this node is a comment or not.
	 */
	public void setComment(final boolean b) {
		comment = b;
	}

	/**
	 * Set whether this node is a doctype or not.
	 */
	public void setDocType(final boolean b) {
		doctype = b;
	}

	/**
	 * Set whether this node is invisible or not.
	 */
	public void setInvisible(final boolean b) {
		if (b) {
			name = null;
		}
	}

	/**
	 * Set whether this node is a processing instruction or not.
	 */
	public void setPI(final boolean b) {
		pi = b;
	}

	/**
	 * Set the plaintext contained in this node.
	 */
	public void setPlaintext(final String str) {
		value = unescapeXml(str);
	}

	// not entirely necessary, but allows XMLNode's to be output
	// int XML by calling .toString() on the root node.
	// Probably wants some indentation handling?
	/**
	 * Turn this node into a String. Outputs the node as XML. So a large amount
	 * of output.
	 */
	@Override
	public String toString() {
		if (isComment())
			return "<!-- " + value + " -->";
		if (isDocType())
			return "<!DOCTYPE " + value + ">";
		if (value != null)
			return value;

		final StringBuffer tmp = new StringBuffer();

		if (!isInvisible()) {
			tmp.append("<");
			if (isPI()) {
				tmp.append("?");
			}
			tmp.append(name);
		}

		final Enumeration<Object> enm = enumerateAttr();
		while (enm.hasMoreElements()) {
			tmp.append(" ");
			final String obj = (String) enm.nextElement();
			tmp.append(obj);
			tmp.append("=\"");
			tmp.append(getAttr(obj));
			tmp.append("\"");
		}
		if (isEmpty()) {
			if (isPI()) {
				tmp.append("?>");
			} else {
				if (!isInvisible()) {
					tmp.append("/>");
				}
			}
		} else {
			if (!isInvisible()) {
				tmp.append(">");
			}

			tmp.append(bodyToString());

			if (!isInvisible()) {
				tmp.append("</" + name + ">\n");
			}
		}
		return tmp.toString();
	}

}
