<style scoped>
    /*.query-button {*/
        /*position: relative;*/
        /*top: -32px;*/
    /*}*/
</style>
<template>
    <div>
        <Row type="flex" style="margin-left: 20px;margin-top: 20px;">
            <Col span="24">
            <Col span="24">
                <span>对</span>
                <Select v-model="table" style="width:360px" @on-change="changeTable">
                    <Option v-for="item in tableList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
                <!--<Select v-model="partition"  style="width:160px">-->
                    <!--<Option v-for="item in partitionList" :value="item.value" :key="item.value">{{ item.label }}</Option>-->
                <!--</Select>-->
            <Select v-model="dateMode" style="width:100px" @on-change="changeDateMode">
                <Option v-for="item in dateModeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>

            <template v-if="dateMode == 'any'">
                <DatePicker type="datetimerange" v-model="timeFrame" placeholder="选择查询分区" style="width: 300px"></DatePicker>
            </template>
            <template v-else>
                <Select v-model="timeFrame" style="width:100px">
                    <Option v-for="item in lastTimeFrameList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
            </template>

            <span>的</span>
                <Select v-model="select" multiple style="width:200px">
                    <Option v-for="item in columnList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
                <span>字段</span>
            </Col>
            <Col span="24" style="margin-top: 20px; margin-bottom: 20px">
            <span>以</span>
            <Input v-model="where" placeholder="where condition" style="width: 525px"></Input>
            <span>条件进行过滤</span>
            <Select v-model="order"  style="width:100px">
                <Option v-for="item in orderList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
            <span>的前</span>
            <Input v-model="limit" placeholder="100" style="width: 100px"></Input>
            <Button class="query-button" @click="mixQuery" type="primary">查询</Button>
            </Col>
            <Spin size="large" fix v-if="spinShow"></Spin>
            </Col>
        </Row>
    </div>
</template>
<script>
    import moment from 'moment';
    export default {
        data () {
            return {
                sqlString: "select [columns] from [table] where [timeFrame] [where] [group by] [order by] [limit]",
                buttonToRight: -100,
                columnList: [],
                tableList: [],
                dateModeList: [
                    {
                        value: 'any',
                        label: '任意时段'
                    },
                    {
                        value: 'last',
                        label: '最近时段'
                    }
                ],
                lastTimeFrameList: [
                    {
                        value: 1,
                        label: '最近一小时'
                    },
                    {
                        value: 3,
                        label: '最近三小时'
                    },
                    {
                        value: 6,
                        label: '最近六小时'
                    },
                    {
                        value: 12,
                        label: '最近十二小时'
                    },
                    {
                        value: 24,
                        label: '最近一天'
                    }
                ],
                orderList: [
                    {
                        value: 'any',
                        label: '任意'
                    },
                    {
                        value: 'new',
                        label: '最新'
                    }
                ],
                metas: [],
                select: ['*'],
                table:'',
                dateMode: 'last',
                timeFrame: 1,
                where: '',
                order: 'new',
                limit: '50',
                grain: 6,
                logContextAttr: {}
            }
        },
        props: {
            spinShow: {type: Boolean},
            refresh: {type: Boolean}
        },
        computed: {


        },
        methods: {
            getSQL() {
                //select [columns] from [table] where [timeFrame] [where] [group by] [order by] [limit]
                let sql = this.sqlString;

                let columns = this.select.includes('*') ? '*' : this.select.join();

                sql = sql.replace('[columns]', columns);

                sql = sql.replace('[table]', this.table);

                let timeFrame = this.sqlTimeFrameCondition();

                let logContextSQL = sql.replace('[timeFrame]', timeFrame);

                sql = logContextSQL;

                let where = this.where === '' ? '' : " and (" + this.where + ")";

                sql = sql.replace('[where]', where);

                sql = sql.replace('[group by]', '');

                let order = this.order === 'any' ? '' : 'order by NEW_DOC, timestamp desc';

                sql = sql.replace('[order by]', order);

                sql = sql.replace('[limit]', 'limit ' + this.limit);

                console.log(this.timeFrame);

                return [sql, logContextSQL];
            },
            getCountSQL() {
                let sql = this.sqlString;

                let columns = "count(*), grain" + this.grain;

                sql = sql.replace('[columns]', columns);

                sql = sql.replace('[table]', this.table);

                let timeFrame = this.sqlTimeFrameCondition();

                sql = sql.replace('[timeFrame]', timeFrame);

                let where = this.where === '' ? '' : " and (" + this.where + ")";

                sql = sql.replace('[where]', where);

                sql = sql.replace('[group by]', 'group by grain' + this.grain);

                sql = sql.replace('[order by]', '');

                sql = sql.replace('[limit]', '');

                return sql;

            },
            getLogContext() {
                return this.logContextAttr[this.table];
            },
            mixQuery() {
                this.query();
                this.changeHistogram(this.getCountSQL());
            },
            query() {
                const sqlArray = this.getSQL();
                this.$emit('query', sqlArray[0], sqlArray[1], this.getLogContext());
            },
            dateConvertTimestamp(moment) {
                return moment.format("YYYY-MM-DD HH:mm:ss");
            },
            dateConvertCondition(date) {
                let day    = date.getDate();
                let month  = date.getMonth() + 1;
                let year   = date.getFullYear();
                let hour   = date.getHours();
                let min    = date.getMinutes();
                let second = date.getSeconds();

                month  = month < 10 ? '0' + month : month;
                day    = day < 10 ? '0' + day : day;
                hour   = hour < 10 ? '0' + hour : hour ;
                min    = min  < 10 ? '0' + min  : min;
                second = second < 10 ? '0' + second : second;

                return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + second;
            },
            anyDateCondition() {
                return "pr >= '" + this.dateConvertCondition(this.timeFrame[0])
                    + "' and pr < '" + this.dateConvertCondition(this.timeFrame[1]) + "'";
            },
            lastDateCondition() {
                let upMoment  = moment();
                let lowMoment = moment().add(-this.timeFrame, 'hours');
                return "pr >= '" + this.dateConvertTimestamp(lowMoment)
                    + "' and pr < '" + this.dateConvertTimestamp(upMoment) + "'";
            },
            sqlTimeFrameCondition() {
                if (this.dateMode === 'any') {
                    return this.anyDateCondition();
                }
                else {
                    return this.lastDateCondition();
                }
            },
            changeDateMode() {
                console.log(this.dateMode);

                if (this.dateMode === 'any') {
                    this.setTimeFrame();
                }
                else {
                    this.timeFrame = 1;
                }
            },
            setTimeFrame() {
                let upMoment  = moment();
                let lowMoment = moment().add(-1, 'hours');
                this.timeFrame = [];
                this.timeFrame[0] = new Date(this.dateConvertTimestamp(lowMoment));
                this.timeFrame[1] = new Date(this.dateConvertTimestamp(upMoment));
                console.log(this.timeFrame);
            },
            changeTable() {
                this.table = this.table === '' ? this.tableList[0].value : this.table;
                for (let tableMeta of this.metas) {
                    if (tableMeta.table === this.table) {
                        this.columnList.length = 0;
                        this.columnList.push({
                            value: '*',
                            label: '全部'
                        });
                        for (let key in tableMeta) {
                            if (tableMeta.hasOwnProperty(key) && key.startsWith("2")) {
                                for (let column of Object.keys(tableMeta[key])) {
                                    this.columnList.push({
                                        value: column,
                                        label: column
                                    });
                                }
                            }
                        }
                    }
                }
                console.log(this.columnList);
                this.mixQuery();
            },
            changeHistogram(sql) {
                this.$emit('changeHistogram', sql);
            },
            getLastMetas() {
                const self = this;
                const notice = this.$Notice;
                this.$http.post(this.ajaxApiUrl + "meta/lastMetas")

                    .then(function (response) {
                        self.metas = response.data;
                        for (let tableMeta of self.metas) {
                            console.log(tableMeta.table);
                                self.tableList.push({
                                    value: tableMeta.table,
                                    label: tableMeta.table
                                });
                        }
                        self.changeTable();
                    })
                    .catch(function (error) {
                        notice.success({
                            title: '获取元数据失败',
                            desc: error
                        });
                    });

            },
            getLogContextAttr() {
                const self = this;
                const notice = this.$Notice;
                this.$http.get(this.ajaxApiUrl + "meta/logContext")

                    .then(function (response) {
                        const logAttrs = response.data;
                        for (let logAttr of logAttrs) {
                            const table = logAttr.table;
                            const columnKeys = logAttr.columnKeys === null ? [] : logAttr.columnKeys.split(",");
                            const context = logAttr.context;

                            self.logContextAttr[table] = {
                                "context" : context,
                                "columnKeys" : columnKeys
                            };
                        }
                    })
                    .catch(function (error) {
                        notice.success({
                            title: '获取日志上下文配置失败',
                            desc: error
                        });
                    });
            },
        },
        mounted() {
            this.getLogContextAttr();
            this.getLastMetas();
        }
    }
</script>
