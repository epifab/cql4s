FROM cassandra:4.0

COPY entrypoint-wrap.sh /entrypoint-wrap.sh
COPY init.cql /tmp/init.cql
ENTRYPOINT ["/entrypoint-wrap.sh"]
CMD ["cassandra", "-f"]
