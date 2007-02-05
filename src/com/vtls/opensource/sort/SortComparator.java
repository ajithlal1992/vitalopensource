package com.vtls.opensource.sort;

import com.vtls.opensource.filters.comparison.accessors.AccessorStrategy;
import com.vtls.opensource.filters.comparison.accessors.DefaultAccessorStrategy;
import com.vtls.opensource.normalization.NormalizationFilter;
import com.vtls.opensource.normalization.filters.ISODateFilter;
import com.vtls.opensource.normalization.filters.LeadingZeroFilter;
import com.vtls.opensource.normalization.filters.LowercaseFilter;
import com.vtls.opensource.logging.Log4JLogger;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SortComparator extends LinkedList implements Comparator
{
   private static final Logger log = Log4JLogger.getLogger(SortComparator.class);
   private AccessorStrategy strategy = null;
   
   public static final int NUMERIC = 1;
   public static final int STRING = 2;
   public static final int DATE = 4;

   public SortComparator(AccessorStrategy strategy)
   {
      // Define a default strategy for obtaining the values to compare.
      this.strategy = strategy;
   }

   public SortComparator()
   {
      this(new DefaultAccessorStrategy());
   }
   
   ///////////////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////

   public void addAscendingSort(Sort _field, NormalizationFilter _filter)
   {
      _field.setDirection(Sort.ASCENDING);
      this.addSort(_field, _filter);
   }

   public void addAscendingSort(Sort _field)
   {
      this.addAscendingSort(_field, null);
   }

   public void addDescendingSort(Sort _field, NormalizationFilter _filter)
   {
      _field.setDirection(Sort.DESCENDING);
      this.addSort(_field, _filter);
   }

   public void addDescendingSort(Sort _field)
   {
      this.addDescendingSort(_field, null);
   }

   public void addSort(Sort _field, NormalizationFilter _filter)
   {
      // Set the NormalizationFilter if any.
      if(_filter != null)
         _field.setFilter(_filter);
         
      super.add(_field);
   }

   public void addSort(Sort _field)
   {
      this.addSort(_field, null);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Comparator /////////////////////////////////////////////////////////////
   
   public int compare(Object a, Object b)
   {
      if(!strategy.hasSupportForObject(a) || !strategy.hasSupportForObject(b))
         return 0;

      if(super.size() == 0)
         return 0;
      
      for(int i = 0; i < super.size(); i++)
      {
         Sort Sort = (Sort)super.get(i);
         String field = Sort.getField();

         if(field != null)
         {
            return compare(Sort, strategy.getFieldValue(a, field),
               strategy.getFieldValue(b, field));
         }
         else
         {
            return compare(Sort, strategy.getValue(a), strategy.getValue(b));
         }
      }

      return 0;
   }

   protected int compare(Sort _field, Object _a, Object _b)
   {
      String field = _field.getField();
      NormalizationFilter filter = _field.getFilter();
      
      int multiplier = (_field.getDirection()) ? 1 : -1;

      if(_a == null && _b == null)
         return 0;
         
      else if(_a != null && _b != null)
      {
         if(!_a.equals(_b))
         {
            String a = (filter != null) ? filter.getString(_a.toString()) : _a.toString();
            String b = (filter != null) ? filter.getString(_b.toString()) : _b.toString();
            
            if(_field.getType() == SortComparator.NUMERIC)
               return multiplier * compareNumericString(a, b);
            else
               return multiplier * compareString(a, b);
         }
      }
      else
         return ((_a != null) ? (-1) : (1));
         
      return 0;
   }

   protected static int compareNumericString(String _a, String _b)
   {
      // TODO. Handle decimals/negatives?
      
      if(_a.length() == _b.length())
         return compareString(_a, _b);
      else if(_a.length() > _b.length())
         return 1;
      else if(_a.length() <= _b.length())
         return -1;
      else return 0;
   }

   protected static int compareString(String _a, String _b)
   {
      // TODO: private static Collator collator = Collator.getInstance();
      // TODO: CollationKey collationKeyA = collator.getCollationKey(_a);

      if(_a == null || _a.length() == 0)
         return -1;
      else if(_b == null || _b.length() == 0)
         return 1;
      else
         return _a.compareTo(_b);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Convenience Methods ////////////////////////////////////////////////////

   public static Sort Numeric(String _field)
   {
      return new Sort(_field,
         SortComparator.NUMERIC,
         Sort.ASCENDING,
         null);
   }
   
   public static Sort String(String _field)
   {
      return new Sort(_field,
         SortComparator.STRING,
         Sort.ASCENDING,
         null);
   }

   public static Sort Date(String _field)
   {
      return new Sort(_field,
         SortComparator.DATE,
         Sort.ASCENDING,
         new ISODateFilter());
   }
   
   public static Sort Numeric()
   {
      return Numeric(null);
   }
   
   public static Sort String()
   {
      return String(null);
   }
   
   public static Sort Date()
   {
      return Date(null);
   }
}
