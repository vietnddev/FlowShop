package com.flowiee.pms.inventory.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_goods_item_temp")
@Getter
@Setter
public class TransactionGoodsItemTemp extends TransactionGoodsItem {
}