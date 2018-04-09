<style scoped>
    body {
        font-family: "Helvetica Neue", Helvetica, Arial;
        font-size: 14px;
        line-height: 20px;
        font-weight: 400;
        color: #3b3b3b;
        -webkit-font-smoothing: antialiased;
        font-smoothing: antialiased;
        background: #2b2b2b;
    }

    .wrapper {
        margin: 0 auto;
        padding: 40px;
        max-width: 800px;
    }

    .table {
        margin: 0 0 40px 0;
        width: 100%;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
        display: table;
    }
    table th{
        white-space: nowrap;
    }
    table td{
        white-space: nowrap;
    }
    @media screen and (max-width: 580px) {
        .table {
            display: block;
        }
    }

    .row {
        display: table-row;
        background: #f6f6f6;
    }
    .row:nth-of-type(odd) {
        background: #e9e9e9;
    }
    .row.header {
        font-weight: 900;
        color: #ffffff;
        background: #ea6153;
    }
    .row.green {
        background: #27ae60;
    }
    .row.blue {
        background: #2980b9;
    }
    @media screen and (max-width: 580px) {
        .row {
            padding: 8px 0;
            display: block;
        }
    }

    .cell {
        padding: 6px 12px;
        display: table-cell;
    }
    @media screen and (max-width: 580px) {
        .cell {
            padding: 2px 12px;
            display: block;
        }
    }
    .page {
        text-align:center;
    }
</style>
<template>
    <div>
        <Row type="flex">
            <Col span="24">
                <ve-histogram :data="chartData" :settings="chartSettings" ref="chart"></ve-histogram>
                <Spin size="large" fix v-if="countShow"></Spin>
            </Col>
            <Col span="24">

                <div class="table">
                    <div class="row header blue">
                        <div class="cell" style="width: 150px">
                            时间
                        </div>
                        <div class="cell" style="width: 100px">
                            ip
                        </div>
                        <div class="cell">
                            内容
                        </div>
                    </div>
                    <div class="row" v-for="row in data">
                        <div class="cell">
                            {{row.timestamp}} <br/> <a @click="getLogContext(row)">查找上下文</a>
                        </div>
                        <div class="cell">
                            {{row.ip}}
                        </div>
                        <div class="cell">
                            <span v-for="(value, key, index) in row.message">
                                <b>{{key}}</b>: <span>{{value}}&nbsp&nbsp</span>
                            </span>
                        </div>
                    </div>
                </div>
            </Col>
        </Row>
        <Modal
                v-model="logContextModal"
                title="日志上下文查询"
                @on-ok="ok"
                cancelText=""
                width="1280">
            <Row type="flex">
                <Col span="24">
                <div class="table">
                    <div class="row header blue">
                        <div class="cell">
                            偏移
                        </div>
                        <div class="cell" style="width: 150px">
                            时间
                        </div>
                        <div class="cell" style="width: 100px">
                            ip
                        </div>
                        <div class="cell">
                            内容
                        </div>
                    </div>
                    <div class="row" v-for="row in logContextData">
                        <div class="cell">
                            {{row.contextOffset}}
                        </div>
                        <div class="cell">
                            {{row.timestamp}}
                        </div>
                        <div class="cell">
                            {{row.ip}}
                        </div>
                        <div class="cell">
                            <span v-for="(value, key, index) in row.message">
                                <b>{{key}}</b>: <span>{{value}}&nbsp&nbsp</span>
                            </span>
                        </div>
                    </div>
                </div>
                <Spin size="large" fix v-if="spinShow"></Spin>
                </Col>
            </Row>
            <div class="page">
                <Button type="primary" @click="upPage">上一页</Button>
                <Button type="primary" @click="downPage">下一页</Button>
            </div>
        </Modal>
    </div>

</template>
<script>
    import moment from 'moment';
    import VCharts from 'v-charts';

    export default {
        data () {
            return {
                logContextModal: false,
                logContextData: [],
                page: 1,
                pageNum: 20,
                spinShow: true,
                currentRow: {},
                chartData: {
                    columns: ['时间', '日志条数'],
                    rows: []
                },
                chartSettings: {
                    metrics: ['日志条数'],
                    dimension: ['时间']
                }
            }
        },
        props: {
            data: {type: Array},
            logContext: {type: Object},
            logContextSQL: {type: String},
            countRows: {type: Array},
            countShow: {type: Boolean}
        },
        watch:{
            countRows: function(val) { //此处不要使用箭头函数
                this.chartData.rows = val;
                console.log(val);
            }
        },
        methods: {
            getLogContext(row) {
                this.currentRow = row;
                this.logContextModal = true;
                const columnKeys = this.logContext.columnKeys;
                console.log("test : " + this.logContext.columnKeys);
                const contextColumn = this.logContext.context;
                const sql = this.getThisRowLogContextSQL(row, columnKeys, contextColumn);
                this.getLogContextData(sql);
            },
            getLogContextData(sql) {
                const self = this;
                const notice = this.$Notice;
                this.$http.post(this.ajaxApiUrl + "search/sql", sql, {
                    headers: { 'Content-Type': 'text/plain' }
                })

                    .then(function (response) {
                        notice.success({
                            title: '上下文查询成功',
                        });
                        self.spinShow = false;
                        const data = response.data;
                        self.refreshData(data);

                    })
                    .catch(function (error) {
                        notice.success({
                            title: '上下文查询失败',
                            desc: error
                        });
                        self.spinShow = false;

                    });

            },
            refreshData (obj) {
                const data    = obj.data;
                const arr = [];

                let contextOffset = this.page > 0 ? (this.page - 1) * this.pageNum : (this.page + 1) * this.pageNum - 1;

                for (let i = 0; i < data.length; i++) {
                    const row = data[i];
                    const instance = {};

                    const timestamp = row['timestamp'];
                    delete row['id'];
                    delete row['offset'];
                    delete row['timestamp'];
                    let ip;
                    if (row.hasOwnProperty('ip')) {
                        ip = row['ip'];
                        delete row['ip'];
                    }
                    else {
                        ip = row['_ip'];
                        delete row['_ip'];
                    }

                    instance['contextOffset'] = this.page > 0 ? contextOffset++ : contextOffset--;

                    instance['timestamp'] = moment.unix(timestamp/1000).format("YYYY-MM-DD HH:mm:ss");
                    instance['ip'] = ip;

                    instance['message'] = row;

                    arr[i] = instance;
                }
                this.logContextData = arr;
            },
            getThisRowLogContextSQL(row, columnKeys, contextColumn) {
                let sql = this.logContextSQL;

                let where = this.getThisWhereCondition(row, columnKeys, contextColumn);

                where = where === '' ? '' : " and (" + where + ")";

                sql = sql.replace('[where]', where);

                let order = this.getOrderBy(contextColumn);

                sql = sql.replace('[order by]', order);

                let limit = this.getLimit();

                sql = sql.replace('[limit]', limit);

                console.log(sql);

                return sql;
            },
            getThisWhereCondition(row, columnKeys, contextColumn) {
                const whereSplit = [];
                for (let columnKey of columnKeys) {
                    if (columnKey === 'ip' || columnKey === 'timestamp') {
                        whereSplit.push(columnKey + " = '" + row[columnKey] + "'");
                    }
                    else {
                        whereSplit.push(columnKey + " = '" + row.message[columnKey] + "'");
                    }
                }

                if (this.page > 0) {
                    if (contextColumn === 'ip' || contextColumn === 'timestamp') {
                        whereSplit.push(contextColumn + " >= '" + row[contextColumn] + "'");
                    }
                    else {
                        whereSplit.push(contextColumn + " >= '" + row.message[contextColumn] + "'");
                    }
                }
                else {
                    if (contextColumn === 'ip' || contextColumn === 'timestamp') {
                        whereSplit.push(contextColumn + " < '" + row[contextColumn] + "'");
                    }
                    else {
                        whereSplit.push(contextColumn + " < '" + row.message[contextColumn] + "'");
                    }
                }

                return whereSplit.join(" and ");
            },
            getOrderBy(contextColumn) {
                const orderByState = this.page > 0 ? 'asc' : 'desc';
                return "order by " + contextColumn + " " + orderByState;
            },
            getLimit() {
                const from = (Math.abs(this.page) - 1) * this.pageNum;
                const len  = this.pageNum;
                return from + ", " + len;
            },
            ok () {
                this.logContextData = [];
                this.page = 1;
            },
            upPage () {
                if (this.page === 1) {
                    this.page = -1;
                }
                else {
                    this.page -= 1;
                }
                this.spinShow = true;
                this.getLogContext(this.currentRow);
            },
            downPage () {
                if (this.page === -1) {
                    this.page = 1;
                }
                else {
                    this.page += 1;
                }
                this.spinShow = true;
                this.getLogContext(this.currentRow);
            }
        },
        mounted() {
        }
    }
</script>