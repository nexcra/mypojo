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

import flash.utils.Dictionary;

import mx.collections.ICollectionView;
import mx.utils.UIDUtil;

/**
 * Static class that defines properties and methods with regard to Hangul (Korean character) data filtering.
 */
public class HangulFilter
{
	/******* 
	 * first consonants : ("ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ");
	 * vowels : ("ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ");
	 * last consonants : ("", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ");
	 */
	
	 
	private static const first_consonant:Array = ["ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"];
	private static const vowels:Array = ["ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ", "ㅡ", "ㅢ", "ㅣ"];
	private static const last_consonant:Array = ["", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"];
	
	/**
	 * @private 
	 * 
	 * An array of codes for all first consonants when they are used on their own.
	 * 
	 **/
	private static const first_consonant_codes:Array = ['12593', '12594', '12596', '12599', '12600','12601', '12609', '12610', '12611', '12613', '12614', '12615', '12616', '12617', '12618', '12619', '12620', '12621', '12622'];
	
	/**
	 * Returns matching index of a given first consonant.
	 * 
	 * @param num Integer type of text code of a first consonant
	 * 
	 * @return Returns a matching index from internal first_consonant_codes array for a given character code.
	 * 
	 */
	private static function returnMatchedString(num:int):int 
	{
		var len:int = first_consonant_codes.length;
		
     	for (var i:int=0; i<len;i++) 
     	{
      		if (Number(first_consonant_codes[i]) == num) 
      		{
	       		return i;
     		 }
 		}
    	return 0;
   }
   
   	/**
	 * Saves character-by-character splitted character code of a data in a dictionary
	 * with mx_internal_uid key mapping the ICollecionView items and the dictionary.
	 *  
	 * @param value ICollectionView type of instance to save character code.
	 * @param key  	Key string to search.
	 * @return 		Returns a new Dictionary instance with all the character code data. 
	 * 
	 */
	public static function hashToCharMap(value:ICollectionView, key:String):Dictionary
	{
		var d:Dictionary = new Dictionary(true);
		var len:int = value.length;
		var uid:String;
		for(var i:Number = 0; i<len; i++)
		{
			uid = mx.utils.UIDUtil.getUID(value[i]);
			d[uid] = toCharArray(value[i][key]);
		}
		return d;
	}
	
   	/**
	 * Converts string to an Array of character codes. 
	 *  
	 * @param value String to convert into character codes' array.
	 * @return 		Returns an Array instance with character codes of a string. 
	 * 
	 */
	public static function toCharArray(value:String):Array 
	{
		var counter:int = 0;
		var len:int = value.length;
		var result:Array = [];
		var num:int = 0;
		for(var i:Number = 0;i<len;i++)
		{
			var code:Number = value.charCodeAt(i);
			if (code >= 12593 && code <= 12622) //In case there is only first consontant
	  		{ 
			    num = code;
	  			result.push(HangulFilter.returnMatchedString(num));
	  		}
	  		else if (code>44031) //In case there are both first consonant and a vowel optionally with last consontant.
	  		{ 
	     	 	num = code-44032;
	     	 	var bottomStr:Number =  num % 28;
			    num = Math.floor(num / 28);
			    var middleStr:Number = num % 21;
			    num = Math.floor(num / 21);
			    var topStr:Number = num % 21;
			    result.push(topStr,middleStr,bottomStr);
	    	}
	    	else   //In case the string is not a Hangul character.
	    	{ 
			    num = code;
			    result.push(num);
		   }
		}
		return result;
	}
	
	 /**
	 * Compares a string and an Array so see whether a string is matching the character codes of a given array.
	 * 
	 * @param original	String to compare
	 * @param target	An array with character codes.
	 * @return 			Returns true if original matches the character codes of target array, otherwise false.
	 * 
	 */
	public static function containsChar(original:String,target:Array):Boolean
	{
		var item:Array = toCharArray(original);
		var len:Number = item.length;
		  for (var i:int=0; i<len;i++) {
	     	 var flag:Boolean= isEqual(item[i],target[i]);
	     	 if (i == len-1 && i%3==1) { 
	     	 	return flag;
	     	 }
	     	 if (len%3 == 0 && i == len-2 && flag) {
	     	 	if (item[len-1] == 0 || isEqual(item[len-1], target[len-1])) {
	     	 		return true;
	     	 	} else {
	     	 		if (compareLastAndFirstConsonant(item[len-1], target[len])) {
	     	 			return true;
	     	 		} else {
	     	 			return compareComplexConsonants(item[len-1], target[len-1], target[len]);
	     	 		}
	     	 	}
	     	 }
	     	 if (!flag) {
	     	 	if (len%3 == 0 && i == len-2) {
	     	 		flag = compareVowels(item[len-2], target[len-2]);
	     	 	}
	     	 	return flag;
	     	 } 
	     }
	   	return true;
	}
	
	 /**
	 * Compares last consonant and first consontant's code to see if they are equal or not.
	 * 
	 * @param code1 Last consonant's character code.
	 * @param code2 First consonant's character code. 
	 * @return Returns true if both codes correspond to the same character, otherwise false. 
	 */
	public static function compareLastAndFirstConsonant(code1:int, code2:int):Boolean {
		return last_consonant[code1] == first_consonant[code2];
	}
	
	
	 /**
	 * Internal method to see whether two items are equal or not. 
	 * 
	 * @param item1 Item 1 to compare.
	 * @param item2 Item 2 to compare.
	 * @return Returns true if both items are equal, otherwise false. 
	 */
	private static function isEqual(item1:Object, item2:Object):Boolean {
		return item1 == item2;
	}
	
		 /**
	 * Compares three codes to indicates whether the character users are typing in are in the
	 * range of charaters in the character code table.
	 * 
	 * @param code1 The last consontant's character code that the user has just typed in.
	 * @param code2 The last consonant's character code in the character code table.
	 * @param code3 The first consonant's character code right after the last consonant in the character code table.
	 * @return Returns true if the last consonant user typed in falls in the range of character code table, other wise false.
	 */
	public static function compareComplexConsonants(code1:int, code2:int, code3:int):Boolean {
		var flag:Boolean;
		switch (code1) {
			case 3:
			if (code2 == 1 && code3 ==9) {
				flag = true;
			}
			break;
			
			case 5: //ㄵ
 	 		if (code2==4 && code3==12) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 6: //ㄶ
 	 		if (code2==4 && code3==18) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		
 	 		case 9://ㄺ
 	 		if (code2==8 && code3==1) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 10://ㄻ
 	 		if (code2==8 && code3==6) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 11://ㄼ
 	 		if (code2==8 && code3==7) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 12://ㄽ
 	 		if (code2==8 && code3==9) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 13://ㄾ
 	 		if (code2==8 && code3==16) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 14://ㄿ
 	 		if (code2==8 && code3==17) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		case 15://ㅀ
 	 		if (code2==8 && code3==18) {
 	 			flag = true;
 	 		}
 	 		break;
 	 		
 	 		
 	 		case 18://ㅄ
 	 		if (code2==16 && code3==9) {
 	 			flag = true;
 	 		}
 	 		break;
		}
		return flag;
	}
	
	 /**
	 * Compares vowels' codes to see whether a vowel that the user typed in fall in the range of character code table.
	 * 
	 * @param code1 The vowel's character code that the user has just typed in.
	 * @param code2 The vowel's character code in the character code table.
	 * @return Returns true if the vowel user typed in falls in the range of character code table, other wise false.
	 */
	public static function compareVowels(code1:int, code2:int):Boolean {
		var flag:Boolean;
		switch (code1) {
			case 8:
			if (code2>=9 && code2 <=11) {
				flag = true;
			}
			break;
			
			case 13:
			if (code2>= 14 && code2 <=16) {
				flag = true;
			}
			
			case 18:
			if (code2 == 19) {
				flag = true;
			}
			break;
			
			default:
			
			break;
		}
		return flag;
	}
}
}