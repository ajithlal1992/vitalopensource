package com.vtls.opensource.comparator;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.Collection;

public class GenericComparator extends LinkedList implements Comparator
{
   public static final int NUMERIC = 1;
   public static final int STRING = 2;
   public static final int DATE = 4;

   public static final boolean ASCENDING = true;
   public static final boolean DESCENDING = false;
   
   public GenericComparator()
   {
   }
   
   public void add(ComparatorField _field)
   {
      super.add(_field);
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Fields /////////////////////////////////////////////////////////////////

   public static ComparatorField Numeric(boolean _direction)
   {
      return new ComparatorField(null, _direction, GenericComparator.NUMERIC);
   }
   
   public static ComparatorField String(boolean _direction)
   {
      return new ComparatorField(null, _direction, GenericComparator.STRING);
   }
   
   public static ComparatorField Date(boolean _direction)
   {
      return new ComparatorField(null, _direction, GenericComparator.DATE);
   }
   
   public static ComparatorField Numeric(String _field, boolean _direction)
   {
      return new ComparatorField(_field, _direction, GenericComparator.NUMERIC);
   }
   
   public static ComparatorField String(String _field, boolean _direction)
   {
      return new ComparatorField(_field, _direction, GenericComparator.STRING);
   }
   
   // Unused for now.
   public static ComparatorField Date(String _field, boolean _direction)
   {
      return new ComparatorField(_field, _direction, GenericComparator.DATE);
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Methods ////////////////////////////////////////////////////////////////
   
   public ComparatorField getField(int i)
   {
      if(i < super.size())
         return (ComparatorField)super.get(i);
      else return null;
   }
   
   public int compare(Object a, Object b)
   {
      // This introspection is not ideal. We should make a generic comparison
      // Strategy that we can pass to this class specific to each type of thing
      // we want to compare.
      if(a instanceof Map && b instanceof Map)
         return compareMap((Map)a, (Map)b);
      else
         return compareGenericObject(a, b);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Specific methods. //////////////////////////////////////////////////////

   public int compareMap(Map _a, Map _b)
   {
      if(super.size() == 0)
         return 0;
      
      for(int i = 0; i < super.size(); i++)
      {
         ComparatorField comparator_field = (ComparatorField)super.get(i);
         String field = comparator_field.getField();
         return compare(_a.get(field), _b.get(field), comparator_field);
      }

      return 0;
   }

   public int compareGenericObject(Object _a, Object _b)
   {
      if(super.size() == 0)
         return compareString(_a.toString(), _b.toString(), true);
      
      ComparatorField comparator_field = (ComparatorField)super.get(0);
      return compare(_a, _b, comparator_field);
   }

   ///////////////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////

   protected int compare(Object _a, Object _b, ComparatorField _field)
   {
      int multiplier = (_field.getDirection()) ? 1 : -1;
      String field = _field.getField();

      if(_a == null && _b == null)
         return 0;
      else if(_a != null && _b != null)
      {
         if(!_a.equals(_b))
         {
            String a = _a.toString().toLowerCase();
            String b = _b.toString().toLowerCase();
            
            if(_field.getType() == GenericComparator.NUMERIC)
               return compareNumericString(a, b, _field.getDirection());
            else if(_field.getType() == GenericComparator.STRING)
               return compareString(a, b, _field.getDirection());
            else if(_field.getType() == GenericComparator.DATE)
               return compareString(a, b, _field.getDirection());
         }
      }
      else
         return (_a != null) ? (-1) : (1);
         
      return 0;
   }

   protected static int compareNumericString(String _a, String _b, boolean _direction)
   {
      int multiplier = (_direction) ? 1 : -1;

      if(_a.length() == _b.length())
         return compareString(_a, _b, _direction);
      else if(_a.length() > _b.length())
         return multiplier * 1;
      else if(_a.length() <= _b.length())
         return multiplier * -1;
      else return 0;
   }

   protected static int compareString(String _a, String _b, boolean _direction)
   {
      int multiplier = (_direction) ? 1 : -1;

      if(_a == null || _a.length() == 0)
         return multiplier * -1;
      else if(_b == null || _b.length() == 0)
         return multiplier * 1;
      else
         return multiplier * _a.compareTo(_b);
   }
}
