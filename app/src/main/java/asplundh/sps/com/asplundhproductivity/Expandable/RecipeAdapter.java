package asplundh.sps.com.asplundhproductivity.Expandable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import asplundh.sps.com.asplundhproductivity.R;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class RecipeAdapter extends ExpandableRecyclerAdapter<ParentModel, ChildModel, OuterViewHolder, InnerViewHolder>
{
    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;
    
    private LayoutInflater mInflater;
    private List<ParentModel> mRecipeList;
    Context mContext;
    
    public RecipeAdapter(Context context, @NonNull List<ParentModel> recipeList) {
        super(recipeList);
        mRecipeList = recipeList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    
    @UiThread
    @NonNull
    @Override
    public OuterViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView;
       
        int layout = R.layout.recipe_view;
        
        switch (viewType) {
            default:
            case PARENT_NORMAL:
                recipeView = mInflater.inflate(layout, parentViewGroup, false);
                break;
            /*case PARENT_VEGETARIAN:
                recipeView = mInflater.inflate(R.layout.vegetarian_recipe_view, parentViewGroup, false);
                break;*/
        }
        return new OuterViewHolder(recipeView , mContext);
    }
    
    @UiThread
    @NonNull
    @Override
    public InnerViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView;
        switch (viewType) {
            default:
            case CHILD_NORMAL:
                ingredientView = mInflater.inflate(R.layout.ingredient_view, childViewGroup, false);
                break;
            case CHILD_VEGETARIAN:
                ingredientView = mInflater.inflate(R.layout.vegetarian_ingredient_view, childViewGroup, false);
                break;
        }
        return new InnerViewHolder(ingredientView);
    }
    
    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull OuterViewHolder recipeViewHolder, int parentPosition, @NonNull ParentModel recipe) {
        recipeViewHolder.bind(recipe);
    
        // mRecipeList.get(parentPosition).;
    
        /*recipeViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(AppConstants.TAG , "*************" );
            }
        });*/
        
       // recipeViewHolder.itemView.getParent().
        
        
      //  Log.i(AppConstants.TAG , "onBindParentViewHolder: "  + recipeViewHolder.getAdapterPosition());
        
     //   ImageView mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_Arrow);
    }
    
    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull InnerViewHolder ingredientViewHolder, int parentPosition, int childPosition, @NonNull ChildModel ingredient) {
        ingredientViewHolder.bind(ingredient);
    }
    
    @Override
    public int getParentViewType(int parentPosition) {
        if (mRecipeList.get(parentPosition).isVegetarian()) {
            return PARENT_VEGETARIAN;
        } else {
            return PARENT_NORMAL;
        }
    }
    
    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        ChildModel ingredient = mRecipeList.get(parentPosition).getIngredient(childPosition);
        
        if(childPosition == 0)
            return CHILD_VEGETARIAN;
        else
            return CHILD_NORMAL;
      /*  if (ingredient.isVegetarian()) {
            return CHILD_VEGETARIAN;
        } else {
            return CHILD_NORMAL;
        }*/
    }
    
    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }
    
}
