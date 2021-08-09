<template>
  <div>
    <el-tooltip placement="left-start">
      <div slot="content">
        <template>
          <el-table
              ref="multipleTable"
              :data="tableField"
              tooltip-effect="dark"
              style="width: 100%"
              @selection-change="handleSelectionChange">
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column
                prop="nameZh"
                label="列名"
                width="120">
            </el-table-column>
          </el-table>
        </template>
      </div>
      <el-button style=" float:right">列名选择</el-button>
    </el-tooltip>

    <div>

      <span v-if="tableOperate === 'add' && extraNotShow.indexOf('footer') === -1">当前表格机器总数：
      <el-tag>
        {{ machines.length }}
      </el-tag>
      </span>

      <el-table :data="machines"
                max-height="1000"
                style="width: 100%">
        <!--        <el-table-column-->
        <!--            type="selection"-->
        <!--            width="55">-->
        <!--        </el-table-column>-->
        <el-table-column
            v-if="fieldIsShow['purchaseOrderId']"
            prop="purchaseOrderId"
            label="采购单号"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['purchaseOrderId']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.purchaseOrderId"></el-input>
              <span v-else>{{
                  scope.row.purchaseOrderId
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow.enterStorageReceiptId"
            prop="enterStorageReceiptId"
            label="入库单号"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['enterStorageReceiptId']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.enterStorageReceiptId"></el-input>
              <span v-else>{{
                  scope.row.enterStorageReceiptId
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow['purchaseReturnReceiptId']"
            prop="purchaseReturnReceiptId"
            label="采购退货单号"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['purchaseReturnReceiptId']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.purchaseReturnReceiptId"></el-input>
              <span v-else>{{
                  scope.row.purchaseReturnReceiptId
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow['marketOrderId']"
            prop="marketOrderId"
            label="销售单据"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['marketOrderId']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.marketOrderId"></el-input>
              <span v-else>{{
                  scope.row.marketOrderId
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow['marketReturnReceiptId']"
            prop="marketReturnReceiptId"
            label="销售退货单据"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['marketReturnReceiptId']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.marketReturnReceiptId"></el-input>
              <span v-else>{{
                  scope.row.marketReturnReceiptId
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow.number"
            prop="number"
            label="物品编号"
            width="170">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['number']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.number"></el-input>
              <span v-else>{{
                  scope.row.number
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow.bagNumber && extraNotShow.indexOf('bagNumber') === -1"
            prop="bagNumber"
            label="袋子编号"
            width="170">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['bagNumber']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.bagNumber"></el-input>
              <span v-else>{{
                  scope.row.bagNumber
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow.paijiBarcode && extraNotShow.indexOf('paijiBarcode') === -1"
            prop="paijiBarcode"
            label="拍机堂编号"
            width="170">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['paijiBarcode']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.paijiBarcode"></el-input>
              <span v-else>{{
                  scope.row.paijiBarcode
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['imei']"
            prop="imei"
            label="IMEI号"
            width="130">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['imei']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.imei"></el-input>
              <span v-else>{{
                  scope.row.imei
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['categoryId']"
            prop="categoryId"
            label="品类"
            width="70">
          <template #default="scope">
            <div>
              <el-select size="mini"
                         v-if="$store.state.machineTableField[tableName]['categoryId']['edit'] === 1 && tableOperate === 'add'"
                         v-model="scope.row.categoryId">
                <el-option
                    v-for="index in Object.keys($store.state.machineCategoryCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineCategoryCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-else>{{
                  $store.state.machineCategoryCorr[scope.row.categoryId]
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            v-if="fieldIsShow['brandId']"
            prop="brandId"
            label="品牌"
            width="70">
          <template #default="scope">
            <div>
              <el-select size="mini"
                         v-if="$store.state.machineTableField[tableName]['brandId']['edit'] === 1 && tableOperate === 'add'"
                         v-model="scope.row.brandId"
                         @change="initUpdateMachine(scope.row, 'purchasingChannelId')">
                <el-option
                    v-for="index in Object.keys($store.state.machineBrandCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineBrandCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-else>{{
                  $store.state.machineBrandCorr[scope.row.brandId]
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['type']"
            prop="type"
            label="型号"
            width="80">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['type']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.type"></el-input>
              <span v-else>{{
                  scope.row.type
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['sku']"
            prop="sku"
            label="sku名称"
            width="150">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['sku']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.sku"></el-input>
              <span v-else>{{
                  scope.row.sku
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['rank']"
            prop="rank"
            label="等级"
            width="70">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['rank']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.rank"></el-input>
              <span v-else>{{
                  scope.row.rank
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['purchasePrice']"
            prop="purchasePrice"
            label="采购价"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['purchasePrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.purchasePrice"></el-input>
              <span v-else>{{
                  scope.row.purchasePrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['qualityInspector']"
            prop="qualityInspector"
            label="质检方"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['qualityInspector']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.qualityInspector"></el-input>
              <span v-else>{{
                  scope.row.qualityInspector
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['salePrice']"
            prop="salePrice"
            label="出库价"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['salePrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.salePrice"></el-input>
              <span v-else>{{
                  scope.row.salePrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['onePrice'] && extraNotShow.indexOf('onePrice') === -1"
            prop="onePrice"
            label="一口价"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['onePrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.onePrice"></el-input>
              <span v-else>{{
                  scope.row.onePrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['bidPrice'] && extraNotShow.indexOf('bidPrice') === -1"
            prop="bidPrice"
            label="竞拍价"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['bidPrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.bidPrice"></el-input>
              <span v-else>{{
                  scope.row.bidPrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['goodPrice'] && extraNotShow.indexOf('goodPrice') === -1"
            prop="goodPrice"
            label="好的价格"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['goodPrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.goodPrice"></el-input>
              <span v-else>{{
                  scope.row.goodPrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['fixPrice']"
            prop="fixPrice"
            label="维修价"
            width="50">
          <template #default="scope">
            <div>
              <el-input maxlength="9" v-show="isEdit"
                        v-if="$store.state.machineTableField[tableName]['fixPrice']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.fixPrice"></el-input>
              <span v-show="!isEdit">{{
                  scope.row.fixPrice
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['describe']"
            prop="describe"
            label="描述"
            width="200">
          <template #default="scope">
            <div>
              <el-input v-show="isEdit"
                        v-if="$store.state.machineTableField[tableName]['describe']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.describe"></el-input>
              <span v-show="!isEdit">{{
                  scope.row.describe
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['stockLocation']"
            prop="stockLocation"
            label="库位"
            width="50">
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['statusId'] && extraNotShow.indexOf('statusId') === -1"
            prop="statusId"
            label="机器状态"
            width="70">
          <template #default="scope">
            <div>
              <!--              <el-select size="mini"-->
              <!--                         v-model="scope.row.statusId"-->
              <!--                         v-if="$store.state.machineTableField[tableName]['statusId']['edit'] === 1 && tableOperate === 'add'">-->
              <!--                <el-option-->
              <!--                    v-for="index in Object.keys($store.state.machineStatusCorr).map(Number)"-->
              <!--                    :key="index"-->
              <!--                    :label="$store.state.machineStatusCorr[index]"-->
              <!--                    :value="index">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
              <span>{{
                  $store.state.machineStatusCorr[scope.row.statusId]
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['purchaseChannelId'] && extraNotShow.indexOf('purchaseChannelId') === -1"
            prop="purchaseChannelId"
            label="购买渠道"
            width="110">
          <template #default="scope">
            <div>
              <el-select size="mini"
                         v-model="scope.row.purchaseChannelId"
                         v-if="$store.state.machineTableField[tableName]['purchaseChannelId']['edit'] === 1 && tableOperate === 'add'">
                <el-option
                    v-for="index in Object.keys($store.state.machineChannelCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineChannelCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-else>{{
                  $store.state.machineChannelCorr[scope.row.purchaseChannelId]
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['saleChannelId']"
            prop="saleChannelId"
            label="出货渠道"
            width="110">
          <template #default="scope">
            <div>
              <el-select size="mini" v-show="isEdit"
                         v-model="scope.row.saleChannelId"
                         v-if="$store.state.machineTableField[tableName]['saleChannelId']['edit'] === 1 && tableOperate === 'add'">
                <el-option
                    v-for="index in Object.keys($store.state.machineChannelCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineChannelCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-else>{{
                  $store.state.machineChannelCorr[scope.row.saleChannelId]
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['operateEmpId']"
            prop="operateEmpId"
            label="处理人"
            width="80">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['purchaseEmpId']"
            prop="purchaseEmpId"
            label="采购人员"
            width="80">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.purchaseEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['enterStorageEmpId']"
            prop="enterStorageEmpId"
            label="入库人员"
            width="80">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.enterStorageEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['purchaseTime'] && extraNotShow.indexOf('purchaseTime') === -1"
            prop="purchaseTime"
            label="采购时间"
            width="160">
          <template #default="scope">
            <div>
              <el-date-picker
                  :clearable="false"
                  v-show="isEdit"
                  v-model="scope.row.purchaseTime"
                  type="date"
                  @change="initUpdateMachine(scope.row, 'purchaseTime')"
                  :disabled="!($store.state.machineTableField[tableName]['purchaseTime']['edit'] === 1 && tableOperate === 'add')"
                  value-format="yyyy-MM-dd"
                  :placeholder="scope.row.purchaseTime" style="width: 130px">
              </el-date-picker>
              <span v-show="!isEdit">{{ scope.row.purchaseTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['enterStorageDate']"
            prop="enterStorageDate"
            label="入库日期"
            width="160">
          <template #default="scope">
            <div>
              <el-date-picker
                  :clearable="false"
                  v-if="$store.state.machineTableField[tableName]['enterStorageDate']['edit'] === 1 && tableOperate === 'add'"
                  v-model="scope.row.enterStorageDate"
                  type="date"
                  @change="initUpdateMachine(scope.row, 'enterStorageDate')"
                  value-format="yyyy-MM-dd"
                  :placeholder="scope.row.enterStorageDate" style="width: 130px">
              </el-date-picker>
              <span v-else>{{ scope.row.enterStorageDate }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['outStorageDate']"
            prop="outStorageDate"
            label="出库日期"
            width="160">
          <template #default="scope">
            <div>
              <el-date-picker
                  :clearable="false"
                  v-if="$store.state.machineTableField[tableName]['outStorageDate']['edit'] === 1 && tableOperate === 'add'"
                  v-model="scope.row.outStorageDate"
                  type="date"
                  @change="initUpdateMachine(scope.row, 'outStorageDate')"
                  value-format="yyyy-MM-dd"
                  :placeholder="scope.row.outStorageDate" style="width: 130px">
              </el-date-picker>
              <span v-else>{{ scope.row.outStorageDate }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['outStorageBatch']"
            prop="outStorageBatch"
            label="出库批次"
            width="110">
          <template #default="scope">
            <div>
              <el-input maxlength="9"
                        v-if="$store.state.machineTableField[tableName]['outStorageBatch']['edit'] === 1 && tableOperate === 'add'"
                        v-model="scope.row.outStorageBatch"
                        @change="initUpdateMachine(scope.row, 'outStorageBatch')"></el-input>
              <span v-else>{{
                  scope.row.outStorageBatch
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['qualityDesc'] && extraNotShow.indexOf('qualityDesc') === -1"
            prop="qualityDesc"
            label="成色检测描述"
            class="qualityDesc"
            width="200px">
          <template #default="scope">
            <div>
              <el-tag v-for="item in (scope.row.qualityDesc === null ? '' : scope.row.qualityDesc.split(','))"
                      :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>

            <!--            <el-form-item :prop="'priceCombination[' + scope.$index +'].iframeBack'"-->
            <!--                          style="margin: 0;"-->
            <!--                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">-->
            <!--            <el-select v-model="scope.row.qualityDesc" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['qualityDesc']['edit'] === 1 && tableOperate === 'add' )"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc[category[scope.row['categoryId']]]['qualityInfos'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc[category[scope.row['categoryId']]]['qualityInfos'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id + ''">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->

            <!--            </el-select>-->
            <!--            </el-form-item>-->
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['featureDesc'] && extraNotShow.indexOf('featureDesc') === -1"
            prop="featureDesc"
            label="功能检测描述"
            width="200px">
          <template #default="scope">
            <div>
              <el-tag v-for="item in (scope.row.featureDesc === null ? '' : scope.row.featureDesc.split(','))"
                      :key="item"
                      v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>
            <!--            <el-select v-model="scope.row.featureDesc" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['featureDesc']['edit'] === 1 && tableOperate === 'add')"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc[category[scope.row['categoryId']]]['functionInfos'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc[category[scope.row['categoryId']]]['functionInfos'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->
            <!--            </el-select>-->
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['rankDesc'] && extraNotShow.indexOf('rankDesc') === -1"
            prop="rankDesc"
            label="等级检测描述"
            width="110">
          <template #default="scope">
            <div>
              <el-input
                  :disabled="!($store.state.machineTableField[tableName]['rankDesc']['edit'] === 1 && tableOperate === 'add')"
                  v-model="scope.row.rankDesc"
                  @change="initUpdateMachine(scope.row, 'rankDesc')"></el-input>
              <!--              <span v-show="!isEdit">{{-->
              <!--                  scope.row.rankDesc-->
              <!--                }}</span>-->
            </div>
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['needFix'] && extraNotShow.indexOf('needFix') === -1"
            prop="needFix"
            label="需要维修的项"
            width="110">
          <template #default="scope">
            <div>
              <el-tag type="warning" v-for="item in (scope.row.needFix === null ? '' : scope.row.needFix.split(','))"
                      :key="item"
                      v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>
            <!--            <el-select v-model="scope.row.needFix" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['needFix']['edit'] === 1 && tableOperate === 'add')"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc['all'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc['all'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id + ''">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->
            <!--            </el-select>-->
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['fixed'] && extraNotShow.indexOf('fixed') === -1"
            prop="fixed"
            label="已修好的项"
            width="110">
          <template #default="scope">
            <div>
              <el-tag type="success" v-for="item in (scope.row.fixed === null ? '' : scope.row.fixed.split(','))"
                      :key="item"
                      v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>
            <!--            <el-select v-model="scope.row.fixed" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['fixed']['edit'] === 1 && tableOperate === 'add')"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc['all'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc['all'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id + ''">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->
            <!--            </el-select>-->
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['notFixed'] && extraNotShow.indexOf('notFixed') === -1"
            prop="notFixed"
            label="未修好的项"
            width="110">
          <template #default="scope">
            <div>
              <el-tag type="danger" v-for="item in (scope.row.notFixed === null ? '' : scope.row.notFixed.split(','))"
                      :key="item"
                      v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>
            <!--            <el-select v-model="scope.row.notFixed" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['notFixed']['edit'] === 1 && tableOperate === 'add')"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc['all'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc['all'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id + ''">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->
            <!--            </el-select>-->
          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['fixToBad'] && extraNotShow.indexOf('fixToBad') === -1"
            prop="fixToBad"
            label="修坏的项"
            width="110">
          <template #default="scope">
            <div>
              <el-tag type="danger" v-for="item in (scope.row.fixToBad === null ? '' : scope.row.fixToBad.split(','))"
                      :key="item"
                      v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </div>
            <!--            <el-select v-model="scope.row.fixToBad" multiple filterable-->
            <!--                       :disabled="!($store.state.machineTableField[tableName]['fixToBad']['edit'] === 1 && tableOperate === 'add')"-->
            <!--                       placeholder="请选择">-->
            <!--              <el-option-group-->
            <!--                  v-for="group in Object.keys($store.state.machineDesc['all'])"-->
            <!--                  style="width: 800px;"-->
            <!--                  :key="group"-->
            <!--                  :label="group">-->
            <!--                <el-option-->
            <!--                    style="display: inline; padding:0 5px; line-height: 0"-->
            <!--                    v-for="item in $store.state.machineDesc['all'][group]"-->
            <!--                    :key="item.id"-->
            <!--                    :label="item.value"-->
            <!--                    :value="item.id + ''">-->
            <!--                </el-option>-->
            <!--              </el-option-group>-->
            <!--            </el-select>-->

          </template>
        </el-table-column>

        <el-table-column
            v-if="fieldIsShow['comment']"
            prop="comment"
            label="备注">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.comment">
              <template #reference>
                <el-button style="width: 90%; text-overflow: ellipsis; overflow: hidden;" v-if="scope.row.comment">
                  {{
                    scope.row.comment
                  }}
                </el-button>
                <el-button style="width: 90%; margin-left: 0;" v-else>无</el-button>
              </template>
              <el-input
                  type="textarea"
                  autosize
                  :disabled="!($store.state.machineTableField[tableName]['comment']['edit'] === 1 && tableOperate === 'add')"
                  :value="scope.row.comment"
                  v-model="scope.row.comment">
              </el-input>
            </el-popover>
          </template>
        </el-table-column>


        <el-table-column label="操作" fixed="right" :width="tableOperate === 'add' ? '80px' : '250px'"
                         v-if="tableOperate === undefined || tableOperate !== 'excel'">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="detail(scope.row)">详情
            </el-button>

            <el-button
                v-if="isRelease === 0 || (tableNameToMachineStatus[tableName] === scope.row.statusId && scope.row.deliverReceiptId === 0 && scope.row.operateEmpId === $store.state.userId) || tableOperate === 'add'"
                size="mini"
                type="danger"
                @click="handleDelete(scope.row, scope.$index)">删除
            </el-button>

            <el-button
                v-if="isRelease === 1 && tableName === 'purchaseReturn' && scope.row.statusId !== 4"
                :disabled="scope.row.statusId === 24"
                size="mini"
                type="danger"
                @click="returnSuccess(scope.row)">退货成功
            </el-button>

            <el-button
                v-if="isRelease === 1 && tableName === 'purchaseReturn' && scope.row.statusId !== 24"
                :disabled="scope.row.statusId === 4"
                size="mini"
                type="danger"
                @click="returnError(scope.row)">退货失败
            </el-button>

            <el-button
                v-if="isRelease === 0 && tableName === 'purchaseOrder'"
                size="mini"
                type="info"
                @click="edit(scope.row)">修改
            </el-button>
            <el-button
                v-if="scope.row.operate !== undefined && scope.row.operate.notSubmitted === 1"
                disabled
                size="mini">未提交
            </el-button>
            <el-button
                v-if="scope.row.operate !== undefined && scope.row.operate.submitted === 1"
                disabled
                size="mini">已提交
            </el-button>

            <el-button
                v-if="scope.row.operate !== undefined && scope.row.operate.edit === 1"
                size="mini"
                type="info"
                @click="$emit('func', scope.row)">修改
            </el-button>

          </template>
        </el-table-column>

      </el-table>
    </div>


    <div style="display: flex; justify-content: flex-end" v-if="paging">
      <el-pagination
          background
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

    <MachineEdit :machine="editMachine" :editShow="editShow" :table-name="tableName"
                 @initShow="initMachinesByApi"></MachineEdit>

    <MachineShowDetailVertical v-if="showDetail.value" :table-name="tableName" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import {getMachine} from "../../api/machineApi";
import * as machineApi from "../../api/machineApi"
import {deleteMachineForEnterStorageReceipt} from "../../api/enterStorageApi";
import {deleteMachineForPurchaseOrder} from "../../api/purchaseOrderReceipt";
import MachineEdit from "./MachineEdit.vue";
import {
  deleteMachineForPurchaseReturnReceipt,
  purchaseReturnError,
  purchaseReturnSuccess
} from "../../api/purchaseReturnApi";
import {deleteMachineForMarketOrderReceipt} from "../../api/marketOrderApi";
import {deleteMachineForMarketReturnReceipt} from "../../api/marketReturnReceiptApi";
import {deleteMachineForMarketReturnEnterStorageReceipt} from "../../api/marketReturnEnterStorageApi";
import {deleteMachineForUpShelfEnterStorage} from "../../api/upShelfEnterStorageApi";
import {getMachineTrace} from "../../api/machineTraceApi";
import MachineShowDetailVertical from "./MachineShowDetailVertical.vue";

export default {
  name: "MachineShowDetail",
  components: {MachineShowDetailVertical, MachineEdit},
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      editShow: {"value": false},
      editMachine: {},
      searchMachine: {},
      fieldIsShow: {
        "number": false,
        "purchaseOrderId": false,
        "enterStorageReceiptId": false,
        "purchaseReturnReceiptId": false,
        "marketOrderId": false,
        "marketReturnReceiptId": false,
        "saleChannelId": false,
        "outStorageDate": false,
        "outStorageBatch": false,
        "bagNumber": false,
        "imei": false,
        "qualityInspector": false,
        "paijiBarcode": false,
        "categoryId": false,
        "brandId": false,
        "type": false,
        "sku": false,
        "rank": false,
        "purchasePrice": false,
        "describe": false,
        "salePrice": false,
        "onePrice": false,
        "bidPrice": false,
        "fixPrice": false,
        "profit": false,
        "stockLocation": false,
        "statusId": false,
        "purchaseChannelId": false,
        "operateEmpId": false,
        "purchaseEmpId": false,
        "enterStorageEmpId": false,
        "purchaseTime": false,
        "enterStorageDate": false,
        "qualityDesc": false,
        "featureDesc": false,
        "rankDesc": false,
        "needFix": false,
        "fixed": false,
        "notFixed": false,
        "fixToBad": false,
        "comment": false
      },
      tableField: [],
      currentPage: 1,
      size: 10,
      total: null,
      isEdit: false,
      category: {1: 'phone', 2: 'tablet', 3: '手表'},
      operateColumnWidth: null,
      tableNameToMachineStatus: {
        "purchaseOrder": 1,
        "purchaseReturn": 3,
        "storageEnter": 2,
        "marketOrder": 13,
        "marketReturn": 14,
        "marketReturnEnterStorage": 2,
        "upShelfEnterStorage": 2,
      }
    }
  },
  props: ['machines', 'tableType', 'paging', 'tableName', 'tableOperate', 'extraNotShow', 'isRelease', 'receiptId'],
  mounted() {
    this.initFieldIsShow()
    this.initTableField()
    this.$nextTick(() => {
      this.tableField.forEach(item => {
        this.$refs.multipleTable.toggleRowSelection(item)
      })
    })
    // if (this.fieldIsShow['qualityDesc'] && this.extraNotShow.indexOf('qualityDesc') === -1) {
    //   this.machines.forEach(item => {
    //     item.qualityDesc = item.qualityDesc.split(",");
    //   })
    // }
    //if (this.fieldIsShow['featureDesc'] && this.extraNotShow.indexOf('featureDesc') === -1) {
    //  this.machines.forEach(item => {
    //    item.featureDesc = item.featureDesc.split(",");
    //  })
    //}
  },

  methods: {
    initTableField() {
      Object.values(this.$store.state.machineTableField[this.tableName]).forEach(item => {
        if (this.tableOperate !== 'add' || item['showWhenAdd'] === 0) {
          this.tableField.push(item)
        }
      })
    },
    initFieldIsShow() {
      let _this = this
      Object.values(this.$store.state.machineTableField[this.tableName]).forEach(item => {
        if (this.tableOperate !== 'add' || item['showWhenAdd'] === 0) {
          _this.fieldIsShow[item['nameEn']] = true
        }
      })
    },
    initMachinesByApi(searchMachine) {
      if (searchMachine !== undefined) {
        this.searchMachine = searchMachine
      }
      getMachine(this.currentPage, this.size, this.searchMachine).then(resp => {
        if (resp.data.obj) {
          this.machines.length = 0
          this.machines.push.apply(this.machines, resp.data.obj.data);
          this.total = resp.data.obj.total
        }
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachinesByApi(this.searchMachine);
    },
    sizeChange(size) {
      this.size = size;
      this.initMachinesByApi(this.searchMachine);
    },
    handleSelectionChange(val) {
      let showField = []
      val.forEach(item => {
        showField.push(item['nameEn'])
      })
      for (let fieldIsShowKey in this.fieldIsShow) {
        if (showField.indexOf(fieldIsShowKey) !== -1) {
          this.fieldIsShow[fieldIsShowKey] = true
          continue;
        }
        this.fieldIsShow[fieldIsShowKey] = false
      }
    },
    /**
     * 添加修改了的字段到this.editMachines中
     */
    //initUpdateMachine(row, fieldName) {
    //  let flag = 1;
    //  for (let i = 0; i < this.editMachines.length; i++) {
    //    let item = this.editMachines[i];
    //    if (item.id === row.id) {
    //      if (this.originalValue[row.id][fieldName] + '' === row[fieldName]) {
    //        delete item[fieldName]
    //        if (Object.keys(item).length === 1) {
    //          this.editMachines.splice(i, 1);
    //          i--;
    //        }
    //      } else {
    //        item[fieldName] = row[fieldName];
    //      }
    //      flag = 0;
    //    }
    //  }
    //  if (flag) {
    //    let machine = {id: row.id}
    //    machine[fieldName] = row[fieldName];
    //    this.editMachines.push(machine);
    //  }
    //},
    //initOriginValue() {
    //  this.machines.forEach(item => {
    //    this.originalValue[item.id] = Object.assign({}, item)
    //  })
    //},
    //handleEdit() {
    //  this.isEdit = true;
    //  this.editMachines = [];
    //  this.initOriginValue();
    //},
    //cancelEdit() {
    //  this.initMachine()
    //  this.isEdit = false;
    //  this.originalValue = {};
    //},
    //completeEdit() {
    //  if (this.editMachines.length !== 0) {
    //    machineApi.modifyMachine(this.editMachines).then(resp => {
    //      this.initMachine();
    //    })
    //  }
    //  this.isEdit = false;
    //  this.editMachines = [];
    //  this.originalValue = {};
    //},
    handleDelete(row, index) {
      if (this.tableOperate === 'add' && index !== undefined) {
        this.machines.splice(index, 1);
        return
      }

      this.$confirm('是否确定删除物品编号为' + row.number + '的机器', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.tableName === 'storageEnter') {
          deleteMachineForEnterStorageReceipt(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'purchaseOrder') {
          deleteMachineForPurchaseOrder(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'purchaseReturn') {
          deleteMachineForPurchaseReturnReceipt(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'marketOrder') {
          deleteMachineForMarketOrderReceipt(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'marketReturn') {
          deleteMachineForMarketReturnReceipt(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'marketReturnEnterStorage') {
          deleteMachineForMarketReturnEnterStorageReceipt(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        } else if (this.tableName === 'upShelfEnterStorage') {
          deleteMachineForUpShelfEnterStorage(row.id).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("删除成功");
              this.initMachinesByApi(this.searchMachine);
            }
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    edit(row) {
      this.editMachine = JSON.parse(JSON.stringify(row))
      this.editShow.value = true;
    },
    returnSuccess(row) {
      this.$confirm('是否确定采购退货成功', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        purchaseReturnSuccess(row.number).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("操作成功");
            this.initMachinesByApi(this.searchMachine);
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    returnError(row) {
      this.$confirm('是否确定采购退货失败', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        purchaseReturnError(row.number).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("操作成功");
            this.initMachinesByApi(this.searchMachine);
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    detail(row) {
      this.showDetailMachine = JSON.parse(JSON.stringify(row));
      getMachineTrace({"number": row.number}).then(resp => {
        this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
      })
      this.showDetail.value = true
    },
  }
}
</script>

<style>
.el-table--small td, .el-table--small td {
  padding: 0 !important;
}

.el-select-group__title {
  padding-left: 6px;
  line-height: 12px;
}

.el-select-group__wrap:not(:last-of-type) {
  padding-bottom: 12px
}

.el-select__tags-text {
  display: inline-block;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>