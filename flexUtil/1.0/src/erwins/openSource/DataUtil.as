////////////////////////////////////////////////////////////////////////////////
//
//  	Copyright (C) 2009 VanillaROI Incorporated and its licensors.
//  	All Rights Reserved. 
//
//
//    	This file is part of OpenZet.
//
//    	OpenZet is free software: you can redistribute it and/or modify
//    	it under the terms of the GNU Lesser General Public License version 3 as published by
//    	the Free Software Foundation. 
//
//    	OpenZet is distributed in the hope that it will be useful,
//    	but WITHOUT ANY WARRANTY; without even the implied warranty of
//    	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    	GNU Lesser General Public License version 3 for more details.
//
//    	You should have received a copy of the GNU Lesser General Public License
//    	along with OpenZet.  If not, see <http://www.gnu.org/licenses/>.
////////////////////////////////////////////////////////////////////////////////
package erwins.openSource{
import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.collections.IList;
import mx.collections.ListCollectionView;
import mx.collections.XMLListCollection;
import mx.utils.UIDUtil;


/**
 * Static class that defines static methods used in relation with various types of data.
 * 
 **/
public class DataUtil
{
	/**
	 * Converts Object type of data into an ArrayCollection instance. 
	 * 
	 * @param value
	 * 
	 * @return A new ArrayCollection instance that is converted from a generic Object data.
	 */
	public static function objectToArrayCollection(value:Object):ArrayCollection
	{
		var ac:ArrayCollection;
	   	if(value is XML)
	   	{
	   		ac = DataUtil.xmlListToArrayCollection(new XML(value).children());
	   	}
	   	else if(value is XMLList)
	   	{
	   		ac = xmlListToArrayCollection(value as XMLList);
	   	}
	   	else if(value is Array)
	   	{
	   		ac = new ArrayCollection(value as Array);
	   	}
	   	else if(value is ICollectionView)
	   	{
	   		ac = objectToCollection(value) as ArrayCollection;
	   	}
	   	else
	   	{
	   		if(!value)
	   		{
	   			throw new Error("value is Empty");
	   			return;
	   		}
	   		ac = new ArrayCollection();
	   		for(var key:String in value)
	   		{
	   			ac.addItem({key:value[key]});
	   		}
	   	}
	   	return ac;
	}
	
	 
	 /**
	 * Converts Object type of data into an ICollectionView instance. 
	 * 
	 * @param value
	 * 
	 * @return A new ICollectionView instance that is converted from a generic Object data.
	 */
	public static function objectToCollection(value:Object):ICollectionView 
	{
		var collection:ICollectionView;
		
        if (value is Array)
        {
            collection = new ArrayCollection(value as Array);
        }
        else if (value is ICollectionView)
        {
            collection = ICollectionView(value);
        }
        else if (value is IList)
        {
            collection = new ListCollectionView(IList(value));
        }
        else if (value is XMLList)
        {
            collection = new XMLListCollection(value as XMLList);
        }
        else if (value is XML)
        {
            var xl:XMLList = new XMLList();
            xl += value;
            collection = new XMLListCollection(xl);
        }
        else
        {
            var tmp:Array = [];
            if (value != null)
                tmp.push(value);
            collection = new ArrayCollection(tmp);
        }
        return collection;
	}
	
	
	/**
	 * Converts XMLList instance into an ArrayCollection instance. 
	 * 
	 * @param xml XMLList type of data to convert into an ArrayCollection.
	 * 
	 * @return A new ArrayCollection instance that is converted from an XMLList instance.
	 * 
	 */
	public static function xmlListToArrayCollection(xml:XMLList):ArrayCollection
	{
		var result:ArrayCollection = new ArrayCollection();
		var xmlLength:int = xml.length();
		for(var i:int = 0; i < xmlLength; i++)
		{
			var row:XMLList = xml[i].children();
			var rowChildLength:int = row.length();
			var resultRow:Object = {};
			for(var j:int = 0 ; j < rowChildLength; j++)
			{
				resultRow[row[j].name()] = row[j]; 
			}
			result.addItem(resultRow); 
		}
		return result;
	}
	
	/**
	 * Applies unique identifier to each row of any implementation of ICollectionView
	 *  
	 * @param target Any ICollectionView type of object to apply unique identifier. 
	 */
	public static function applyUID(target:ICollectionView):void {
		var len:int = target.length;
		for (var i:int = 0; i < len; i++) {
			target[i].mx_internal_uid = mx.utils.UIDUtil.createUID();
		}
	}
	
	/**
	 * Returns a new generic Object wrapping some data.
	 *  
	 * @param data Any type of data to wrap in a generic Object.
	 * @param propertyName A property name of a resulting object so we can access original data through
	 * this propertyName. Default value is 'data'.
	 * 
	 * @return A new generic object wrapping original data with certain property name.
	 *  
	 */
	public static function wrapDataAsObject(data:Object, propertyName:String = "data"):Object {
		var result:Object;
		if (data) {
			result = {};
			result[propertyName] = data;
		}
		return result;
	}
}
}