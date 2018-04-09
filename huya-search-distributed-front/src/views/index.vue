<style scoped lang="less" type="text/less">
    .index{
        h1{
            height: 150px;
            img{
                height: 100%;
            }
        }
        h2{
            color: #666;
            /*margin-bottom: 200px;*/
            p{
                margin: 0 0 50px;
            }
        }
        .ivu-row-flex{
            height: 100%;
        }
    }
    ::-webkit-scrollbar{
        width: 0px;
    }
    .query{
        position: relative;
    }
</style>
<template>
    <div class="index">
        <Row type="flex">
            <Col span="24">
                <SqlQueryInput
                        v-on:query="query"
                        v-on:changeHistogram="changeHistogram"
                        v-bind:refresh="refresh"
                        v-bind:spinShow="spinShow"
                ></SqlQueryInput>
            </Col >
            <Col><h3 style=" margin-bottom: 20px">{{queryInfo}}</h3></Col>
            <Col span="24">
                <DataTable
                        v-bind:data="data"
                        v-bind:logContext="logContext"
                        v-bind:logContextSQL="logContextSQL"
                        v-bind:countRows="countRows"
                        v-bind:countShow="countShow"
                ></DataTable>
            </Col >
        </Row>
    </div>
</template>
<script>
    import qs from 'qs';

    import moment from 'moment';
    import SqlQueryInput from './sqlQueryInput.vue';
    import DataTable from './dataTable.vue';
    export default {
        components:{
            'SqlQueryInput': SqlQueryInput,
            'DataTable': DataTable
        },
        data () {
            return {
                sqlString: "",
                data: [],
                refresh: false,
                spinShow: false,
                countShow: false,
                queryInfo: "",
                logContextSQL: "",
                logContext: {},
                countRows: []
            }
        },
        methods: {
            query (sql, logContextSQL, logContext) {
                const self = this;
                const notice = this.$Notice;
                console.log(sql);
                this.spinShow = true;
                this.$http.post(this.ajaxApiUrl + "search/sql", sql, {
                    headers: { 'Content-Type': 'text/plain' }
                })

                        .then(function (response) {
                        notice.success({
                            title: '查询成功',
                        });
                        self.spinShow = false;
                        const data = response.data;
                        self.refreshData(data, logContextSQL, logContext);

                    })
                    .catch(function (error) {
                        notice.success({
                            title: '查询失败',
                            desc: error
                        });
                        self.spinShow = false;

                    });

            },
            changeHistogram (sql) {
                const self = this;
                const notice = this.$Notice;
                this.countShow = true;
                    this.$http.post(this.ajaxApiUrl + "search/sql", sql, {
                    headers: { 'Content-Type': 'text/plain' }
                })

                    .then(function (response) {
                        self.countShow = false;
                        const data = response.data;
                        self.refreshHistogramData(data);

                    })
                    .catch(function (error) {
                        notice.success({
                            title: '柱状图查询失败',
                            desc: error
                        });
                        self.countShow = false;

                    });
            },
            refreshData (obj, logContextSQL, logContext) {
                this.queryInfo = "查询耗时：" + obj.runtime + " ms";
                // let columnMap = new Map();
                const data    = obj.data;
                const arr = [];
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

                    instance['timestamp'] = moment.unix(timestamp/1000).format("YYYY-MM-DD HH:mm:ss");
                    instance['ip'] = ip;

                    instance['message'] = row;

                    arr[i] = instance;
                }

                this.data = arr;
                this.logContextSQL = logContextSQL;
                this.logContext = logContext;
            },
            refreshHistogramData (obj) {
                const countRows = [];
                let tempObj = {};
                console.log(obj['data']);

                for (let countObj of obj['data']) {
                    tempObj[countObj['timestamp']] = countObj['*'];
                }

                const timeArray = Object.keys(tempObj).sort();
                for (let time of timeArray) {
                    countRows.push({'日志条数': tempObj[time], '时间': time.substr(0, 13)});
                }
                this.countRows = countRows;
            },
            convert (key, value) {
                if (key === "_area" || key === "_isp") {
                    return decodeURIComponent(value);
                }
                return value;
            }
        }
    }
</script>
