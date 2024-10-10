package nus.iss.se.team9.report_review_service.model;

import jakarta.persistence.*;

@Entity
public class ShoppingListItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	private Member member;
	@Column
	private String ingredientName;
	private boolean isChecked;
	
	public ShoppingListItem() {}
	
	public ShoppingListItem(Member member, String ingredientName) {
		this.member = member;
		this.ingredientName = ingredientName;
		isChecked = false;
	}
	
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}