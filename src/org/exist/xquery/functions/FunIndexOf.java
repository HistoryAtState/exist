/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-04 Wolfgang M. Meier
 *  wolfgang@exist-db.org
 *  http://exist.sourceforge.net
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.xquery.functions;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Constants;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.GeneralComparison;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.AtomicValue;
import org.exist.xquery.value.IntegerValue;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceIterator;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.exist.xquery.value.ValueSequence;

/**
 * @author wolf
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FunIndexOf extends BasicFunction {

	public final static FunctionSignature fnIndexOf[] = {
			new FunctionSignature(
					new QName("index-of", ModuleImpl.NAMESPACE_URI, ModuleImpl.PREFIX),
					"Returns a sequence of positive integers giving the positions within the sequence " +
					"$a of items that are equal to $b. If the value of $a is the empty sequence, or if " +
					"no item in $a matches $b, then the empty sequence is returned.",
					new SequenceType[] {
							new SequenceType(Type.ATOMIC, Cardinality.ZERO_OR_MORE),
							new SequenceType(Type.ATOMIC, Cardinality.EXACTLY_ONE)
					},
					new SequenceType(Type.INTEGER, Cardinality.ZERO_OR_ONE)
			),
			new FunctionSignature(
					new QName("index-of", ModuleImpl.NAMESPACE_URI, ModuleImpl.PREFIX),
					"Returns a sequence of positive integers giving the positions within the sequence " +
					"$a of items that are equal to $b. If the value of $a is the empty sequence, or if " +
					"no item in $a matches $b, then the empty sequence is returned. Values are compared " +
					"according to the collation specified in $c.",
					new SequenceType[] {
							new SequenceType(Type.ATOMIC, Cardinality.ZERO_OR_MORE),
							new SequenceType(Type.ATOMIC, Cardinality.EXACTLY_ONE),
							new SequenceType(Type.STRING, Cardinality.EXACTLY_ONE)
					},
					new SequenceType(Type.INTEGER, Cardinality.ZERO_OR_ONE)
			)
	};
	
	public FunIndexOf(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}
	
	/* (non-Javadoc)
	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)
	 */
	public Sequence eval(Sequence[] args, Sequence contextSequence)
			throws XPathException {
		if(args[0].getLength() == 0)
			return Sequence.EMPTY_SEQUENCE;
		AtomicValue srch = args[1].itemAt(0).atomize();
		Collator collator;
		if(getSignature().getArgumentCount() == 3) {
			String collation = args[2].getStringValue();
			collator = context.getCollator(collation);
		} else
			collator = context.getDefaultCollator();
		Sequence result = new ValueSequence();
		int j = 1;
		for (SequenceIterator i = args[0].iterate(); i.hasNext(); j++) {
			AtomicValue next = i.nextItem().atomize();
			if(GeneralComparison.compareAtomic(collator, next, srch, context.isBackwardsCompatible(),
					Constants.TRUNC_NONE, Constants.EQ))
				result.add(new IntegerValue(j));
		}
		return result;
	}

}
